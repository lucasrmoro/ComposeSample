"""Stage assets in a draft, then publish the reviewed commit at its immutable tag."""

import hashlib
import json
import os
import subprocess
import tempfile
from pathlib import Path

ASSETS = ("ComposeSample.apk", "RELEASE_NOTES.md", "release.json", "sbom.cdx.json",
          "signing-certificate-sha256.txt", "SHA256SUMS")


def gh(*args):
    return subprocess.check_output(["gh", *args], text=True).strip()


def tag_commit(tag):
    refs = subprocess.check_output(["git", "ls-remote", "origin", f"refs/tags/{tag}", f"refs/tags/{tag}^{{}}"], text=True)
    return refs.splitlines()[-1].split()[0] if refs else None


def releases():
    repository = os.environ["GITHUB_REPOSITORY"]
    return json.loads(gh("api", f"repos/{repository}/releases?per_page=100", "--paginate", "--slurp"))


def lookup(tag):
    return next((item for page in releases() for item in page if item["tag_name"] == tag), None)


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def match_assets(release, directory, tag):
    existing = {item["name"]: item for item in release["assets"]}
    if set(existing) - set(ASSETS):
        raise SystemExit(f"{tag} has unexpected assets. Review the draft before retrying.")
    for name, item in existing.items():
        expected = f"sha256:{digest(directory / name)}"
        if not item.get("digest"):
            with tempfile.TemporaryDirectory() as temporary:
                gh("release", "download", tag, "--pattern", name, "--dir", temporary)
                actual = f"sha256:{digest(Path(temporary) / name)}"
        else:
            actual = item["digest"]
        if actual != expected:
            raise SystemExit(f"{tag} already has different bytes for {name}. Retry using the original signed artifacts.")
    return set(existing)


def main():
    if os.environ.get("RELEASE_PUBLICATION_ENABLED") != "true":
        raise SystemExit("Enable immutable releases and configure the publication environment before publishing.")
    tag, revision, previous_tag, parent = (os.environ[key] for key in ("TAG", "REVISION", "PREVIOUS_TAG", "PARENT"))
    if previous_tag:
        previous = lookup(previous_tag)
        if not previous or previous["draft"] or not previous.get("immutable") or tag_commit(previous_tag) != parent:
            raise SystemExit(f"Publish the preceding revision at {previous_tag} before retrying {tag}.")
    directory = Path("signed")
    if {path.name for path in directory.iterdir()} != set(ASSETS):
        raise SystemExit("Signed release assets differ from the approved inventory.")
    metadata = json.loads((directory / "release.json").read_text())
    if metadata != dict(sourceCommit=revision, versionName=os.environ["VERSION"],
                        versionCode=int(os.environ["VERSION_CODE"]), flavor="prd"):
        raise SystemExit("Signed artifact metadata does not match the approved production revision.")
    subprocess.run(["sha256sum", "--check", "SHA256SUMS"], cwd=directory, check=True)
    gh("attestation", "verify", str(directory / "ComposeSample.apk"), "--repo", os.environ["GITHUB_REPOSITORY"],
       "--signer-workflow", f"{os.environ['GITHUB_REPOSITORY']}/.github/workflows/android-release.yml")
    current_tag = tag_commit(tag)
    if current_tag and current_tag != revision:
        raise SystemExit(f"Tag {tag} already points to another commit.")
    release = lookup(tag)
    if not release:
        gh("release", "create", tag, "--draft", "--target", revision, "--title", tag,
           "--notes-file", str(directory / "RELEASE_NOTES.md"))
        release = lookup(tag)
    if not release or release["name"] != tag or release["target_commitish"] != revision:
        raise SystemExit("Release name or target does not match the reviewed version.")
    existing = match_assets(release, directory, tag)
    if release["draft"]:
        for name in ASSETS:
            if name not in existing:
                gh("release", "upload", tag, str(directory / name))
        if match_assets(lookup(tag), directory, tag) != set(ASSETS):
            raise SystemExit("Draft release is missing assets.")
        gh("release", "edit", tag, "--draft=false")
    elif existing != set(ASSETS):
        raise SystemExit("Published release has an incomplete asset inventory.")
    final = lookup(tag)
    if tag_commit(tag) != revision or not final or final["draft"] or not final.get("immutable"):
        raise SystemExit("Release must be immutable and its tag must match the approved commit.")


if __name__ == "__main__":
    main()

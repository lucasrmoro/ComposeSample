"""Validate the release decision against its first parent and repository tags."""

import argparse
import re
import subprocess
from pathlib import Path

VERSION = re.compile(r"versionName=((?:0|[1-9][0-9]*)\.(?:0|[1-9][0-9]*)\.(?:0|[1-9][0-9]*))\nversionCode=([1-9][0-9]{0,9})\n?")


def git(*arguments):
    return subprocess.check_output(["git", *arguments], text=True).strip()


def read_version(commit):
    value = VERSION.fullmatch(git("show", f"{commit}:release.properties"))
    if not value or int(value[2]) > 2_100_000_000:
        raise SystemExit("release.properties must contain canonical X.Y.Z and a valid Android versionCode.")
    return value[1], int(value[2])


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("commit")
    parser.add_argument("--release", action="store_true")
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    commit = git("rev-parse", f"{args.commit}^{{commit}}")
    parent = git("rev-parse", f"{commit}^1")
    version, code = read_version(commit)
    previous_tag = ""
    if "release.properties" in git("ls-tree", "--name-only", parent, "--", "release.properties"):
        previous, previous_code = read_version(parent)
        if tuple(map(int, version.split("."))) <= tuple(map(int, previous.split("."))) or code <= previous_code:
            raise SystemExit("Increase both versionName and versionCode over main. Update the branch and rerun validation.")
        previous_tag = f"v{previous}"
    elif code <= 1:
        raise SystemExit("The first release must supersede the legacy Android versionCode 1.")
    notes = git("show", f"{commit}:RELEASE_NOTES.md").splitlines()
    if not notes or notes[0] != f"# Version {version}" or not any(line.strip() for line in notes[1:]):
        raise SystemExit("RELEASE_NOTES.md must contain the exact version heading and release notes.")
    tag = f"v{version}"
    if git("tag", "--list", tag) and (not args.release or git("rev-parse", f"{tag}^{{commit}}") != commit):
        raise SystemExit(f"Release tag {tag} already exists.")
    if args.output:
        with args.output.open("a", encoding="utf-8") as output:
            for key, value in dict(tag=tag, version=version, code=code, commit=commit, parent=parent, previous_tag=previous_tag).items():
                output.write(f"{key}={value}\n")
    print(f"Validated {tag}, Android versionCode {code}, source {commit}.")


if __name__ == "__main__":
    main()

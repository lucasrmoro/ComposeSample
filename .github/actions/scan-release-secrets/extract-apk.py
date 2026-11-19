"""Safely extract APK files so secret scanners can inspect their contents."""

import shutil
import stat
import sys
import zipfile
from pathlib import Path, PurePosixPath


def extract_apk(apk_path: Path, destination: Path) -> None:
    root = destination.resolve()
    root.mkdir(parents=True, exist_ok=True)

    with zipfile.ZipFile(apk_path) as apk:
        for entry in apk.infolist():
            # APK entries use POSIX paths; reject traversal and absolute paths before writing anything.
            member = PurePosixPath(entry.filename)
            if member.is_absolute() or ".." in member.parts or "\\" in entry.filename:
                raise ValueError(f"Unsafe APK entry path: {entry.filename}")

            target = (root / Path(*member.parts)).resolve()
            if target != root and root not in target.parents:
                raise ValueError(f"APK entry escapes extraction directory: {entry.filename}")

            # Do not create symlinks from an archive while preparing files for the scanner.
            file_mode = entry.external_attr >> 16
            if stat.S_ISLNK(file_mode):
                raise ValueError(f"APK contains a symbolic link: {entry.filename}")

            if entry.is_dir():
                target.mkdir(parents=True, exist_ok=True)
                continue

            target.parent.mkdir(parents=True, exist_ok=True)
            with apk.open(entry) as source, target.open("xb") as output:
                shutil.copyfileobj(source, output)


if __name__ == "__main__":
    extract_apk(Path(sys.argv[1]), Path(sys.argv[2]))

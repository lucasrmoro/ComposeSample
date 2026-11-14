"""Translate Android Lint XML errors into GitHub workflow annotations."""

import os
import xml.etree.ElementTree as ET
from pathlib import Path


def main():
    report = Path("app/build/reports/lint-results-devDebug.xml")
    if not report.exists():
        return

    workspace = Path(os.environ.get("GITHUB_WORKSPACE", ".")).resolve()
    root = ET.parse(report).getroot()
    for issue in root.findall("issue"):
        if issue.get("severity", "").lower() != "error":
            continue
        for location in issue.findall("location"):
            source = Path(location.get("file", ""))
            line = location.get("line")
            if not source.is_absolute() or not line:
                continue
            try:
                relative_path = source.resolve().relative_to(workspace).as_posix()
                line_number = int(line)
            except (ValueError, OSError):
                continue
            message = f"{issue.get('id', 'Android Lint')}: {issue.get('message', '')}"
            message = message.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
            print(f"::error file={relative_path},line={line_number}::{message}")


if __name__ == "__main__":
    main()

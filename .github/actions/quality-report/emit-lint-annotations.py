"""Translate Android Lint XML errors into GitHub workflow annotations."""

import os
import xml.etree.ElementTree as ET
from pathlib import Path


def main():
    # Android Lint writes XML after the Gradle task; no report means the task failed before analysis.
    report = Path("app/build/reports/lint-results-devDebug.xml")
    if not report.exists():
        return

    workspace = Path(os.environ.get("GITHUB_WORKSPACE", ".")).resolve()
    root = ET.parse(report).getroot()
    for issue in root.findall("issue"):
        # Warnings remain in the HTML artifact; only errors and fatal findings block the gate.
        if issue.get("severity", "").lower() not in {"error", "fatal"}:
            continue
        for location in issue.findall("location"):
            source = Path(location.get("file", ""))
            line = location.get("line")
            if not source.is_absolute() or not line:
                continue
            try:
                # GitHub annotations use checkout-relative paths and a numeric line in the file.
                relative_path = source.resolve().relative_to(workspace).as_posix()
                line_number = int(line)
            except (ValueError, OSError):
                continue
            message = f"{issue.get('id', 'Android Lint')}: {issue.get('message', '')}"
            # Escape workflow-command control characters so lint text cannot corrupt the annotation.
            message = message.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
            print(f"::error file={relative_path},line={line_number}::{message}")


if __name__ == "__main__":
    main()

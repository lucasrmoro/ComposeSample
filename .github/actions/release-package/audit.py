"""Check Maven runtime dependencies against OSV and emit a CycloneDX inventory."""

import json
import sys
from pathlib import Path
from urllib.parse import quote
from urllib.request import Request, urlopen


def main():
    source, destination = map(Path, sys.argv[1:])
    dependencies = json.loads(source.read_text())
    if not dependencies:
        raise SystemExit("Empty release dependency inventory.")
    queries = [{"package": {"ecosystem": "Maven", "name": f"{d['group']}:{d['name']}"},
                "version": d["version"]} for d in dependencies]
    findings = []
    for start in range(0, len(queries), 100):
        batch = queries[start:start + 100]
        request = Request("https://api.osv.dev/v1/querybatch", data=json.dumps({"queries": batch}).encode(),
                          headers={"Content-Type": "application/json"})
        with urlopen(request, timeout=60) as response:
            results = json.load(response)["results"]
        if len(results) != len(batch):
            raise SystemExit("Incomplete OSV response.")
        for query, result in zip(batch, results):
            findings.extend(f"{query['package']['name']}:{query['version']} {v['id']}" for v in result.get("vulns", []))
    if findings:
        raise SystemExit("Review release dependency advisories:\n" + "\n".join(findings))
    components = [{"type": "library", **d,
                   "purl": f"pkg:maven/{quote(d['group'], safe='')}/{quote(d['name'], safe='')}@{quote(d['version'], safe='')}"}
                  for d in dependencies]
    sbom = {"bomFormat": "CycloneDX", "specVersion": "1.6", "version": 1, "components": components}
    destination.write_text(json.dumps(sbom, indent=2) + "\n")


if __name__ == "__main__":
    main()

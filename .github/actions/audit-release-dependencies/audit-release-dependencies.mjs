import fs from 'node:fs';

// Gradle produces this inventory from the resolved runtime graph for the selected APK flavor.
const inventoryPath = `app/build/reports/release/${process.env.FLAVOR}-dependencies.json`;
const dependencies = JSON.parse(fs.readFileSync(inventoryPath, 'utf8'));
if (!dependencies.length) throw new Error('Empty release dependency inventory.');

// OSV identifies Maven artifacts by their group and artifact coordinates and the resolved version.
const queries = dependencies.map(({ group, name, version }) => ({
    package: { ecosystem: 'Maven', name: `${group}:${name}` },
    version
}));
const findings = [];

// Query resolved runtime versions, including transitive dependencies, in bounded batches.
for (let start = 0; start < queries.length; start += 100) {
    const batch = queries.slice(start, start + 100);
    const response = await fetch('https://api.osv.dev/v1/querybatch', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ queries: batch }),
        signal: AbortSignal.timeout(60000)
    });
    if (!response.ok) throw new Error(`OSV request failed (${response.status}).`);
    const { results } = await response.json();
    if (results.length !== batch.length) throw new Error('Incomplete OSV response.');
    results.forEach((result, index) => {
        const query = batch[index];
        for (const advisory of result.vulns ?? []) {
            findings.push(`${query.package.name}:${query.version} ${advisory.id}`);
        }
    });
}

// Fail the release gate when OSV reports a known advisory so it is reviewed before signing.
if (findings.length) throw new Error(`Review release dependency advisories:\n${findings.join('\n')}`);

// CycloneDX preserves the resolved coordinates in a format supported by dependency scanners.
const components = dependencies.map(({ group, name, version }) => ({
    type: 'library',
    group,
    name,
    version,
    purl: `pkg:maven/${encodeURIComponent(group)}/${encodeURIComponent(name)}@${encodeURIComponent(version)}`
}));
const sbom = { bomFormat: 'CycloneDX', specVersion: '1.6', version: 1, components };
fs.mkdirSync('candidate', { recursive: true });
fs.writeFileSync('candidate/sbom.cdx.json', `${JSON.stringify(sbom, null, 2)}\n`);

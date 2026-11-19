const fs = require('node:fs');
const { createHash } = require('node:crypto');

function getReleaseAssets(apkName) {
    // Keep this allowlist exact so a release contains only the reviewed production deliverables.
    return [apkName, 'RELEASE_NOTES.md', 'release.json', 'sbom.cdx.json',
        'signing-certificate-sha256.txt', 'SHA256SUMS'];
}

async function findRelease(github, context, tag) {
    const releases = await github.paginate(github.rest.repos.listReleases, context.repo);
    return releases.find(release => release.tag_name === tag);
}

async function getTagCommit(github, context, tag) {
    try {
        let { data: { object } } = await github.rest.git.getRef({ ...context.repo, ref: `tags/${tag}` });
        // Annotated Git tags point to tag objects, which in turn point to the actual commit.
        while (object.type === 'tag') {
            ({ data: { object } } = await github.rest.git.getTag({ ...context.repo, tag_sha: object.sha }));
        }
        return object.type === 'commit' ? object.sha : undefined;
    } catch (error) {
        if (error.status !== 404) throw error;
        return undefined;
    }
}

async function verifyPredecessor(github, context, previousTag, parent, nextTag) {
    if (!previousTag) return;
    const previous = await findRelease(github, context, previousTag);
    // Publish in main order; a retry cannot skip an unpublished predecessor.
    if (!previous?.immutable || previous.draft || (await getTagCommit(github, context, previousTag)) !== parent) {
        throw new Error(`Publish ${previousTag} before retrying ${nextTag}.`);
    }
}

function verifyLocalAssets(names, revision, version, versionCode) {
    const localNames = fs.readdirSync('signed').sort();
    if (localNames.join('\n') !== names.slice().sort().join('\n')) {
        throw new Error('Signed release asset inventory differs from policy.');
    }

    const metadata = JSON.parse(fs.readFileSync('signed/release.json', 'utf8'));
    if (metadata.sourceCommit !== revision || metadata.versionName !== version ||
        metadata.versionCode !== Number(versionCode) || metadata.flavor !== 'prd') {
        throw new Error('Signed APK metadata differs from the approved revision.');
    }
}

function verifyAssetDigests(assets, names) {
    const existing = new Set();
    for (const asset of assets) {
        if (!names.includes(asset.name)) throw new Error(`Unexpected release asset ${asset.name}.`);
        const data = fs.readFileSync(`signed/${asset.name}`);
        const digest = `sha256:${createHash('sha256').update(data).digest('hex')}`;
        // A retry may fill missing draft assets, but it must never replace different uploaded bytes.
        if (asset.digest !== digest) {
            throw new Error(`${asset.name} differs from the original release asset. Retry with the original artifact.`);
        }
        existing.add(asset.name);
    }
    return existing;
}

async function removeIncompleteDraftAssets(github, context, release, names) {
    const repo = github.rest.repos;
    const incompleteAssets = release.assets.filter(asset => asset.state === 'starter');

    for (const asset of incompleteAssets) {
        if (!names.includes(asset.name)) {
            throw new Error(`Unexpected incomplete release asset ${asset.name}.`);
        }
        // GitHub can leave a `starter` placeholder after a failed upload; delete only that known incomplete draft asset.
        await repo.deleteReleaseAsset({ ...context.repo, asset_id: asset.id });
    }

    const incompleteIds = new Set(incompleteAssets.map(asset => asset.id));
    return release.assets.filter(asset => !incompleteIds.has(asset.id));
}

async function createDraftIfMissing(github, context, tag, revision, notes) {
    let release = await findRelease(github, context, tag);
    if (release) return release;

    const { data } = await github.rest.repos.createRelease({
        ...context.repo,
        tag_name: tag,
        target_commitish: revision,
        name: tag,
        body: notes,
        draft: true
    });
    return data;
}

async function uploadMissingAssets(github, context, release, names, existing) {
    const repo = github.rest.repos;
    for (const name of names.filter(assetName => !existing.has(assetName))) {
        const data = fs.readFileSync(`signed/${name}`);
        await repo.uploadReleaseAsset({
            ...context.repo,
            release_id: release.id,
            name,
            data,
            headers: { 'content-type': 'application/octet-stream', 'content-length': data.length }
        });
    }

    const assets = await github.paginate(repo.listReleaseAssets, {
        ...context.repo,
        release_id: release.id
    });
    if (verifyAssetDigests(assets, names).size !== names.length) {
        throw new Error('Incomplete draft release.');
    }
    // Make the release public only after every approved asset is present and matches its local checksum.
    await repo.updateRelease({ ...context.repo, release_id: release.id, draft: false });
}

module.exports = async ({ github, context }) => {
    const { TAG: tag, REVISION: revision, PREVIOUS_TAG: previousTag, PARENT: parent,
        VERSION: version, VERSION_CODE: versionCode, APK_NAME: apkName } = process.env;
    if (process.env.RELEASE_PUBLICATION_ENABLED !== 'true') {
        throw new Error('Enable immutable releases and configure the publication environment.');
    }

    await verifyPredecessor(github, context, previousTag, parent, tag);
    const names = getReleaseAssets(apkName);
    verifyLocalAssets(names, revision, version, versionCode);

    const existingTag = await getTagCommit(github, context, tag);
    if (existingTag && existingTag !== revision) throw new Error(`${tag} points to another revision.`);

    const release = await createDraftIfMissing(
        github,
        context,
        tag,
        revision,
        fs.readFileSync('signed/RELEASE_NOTES.md', 'utf8')
    );
    if (release.name !== tag || release.target_commitish !== revision) {
        throw new Error('Release name or target differs from the approved revision.');
    }

    if (release.draft) {
        // A failed upload may leave an empty `starter` asset; remove it so this retry can upload the original bytes.
        const completedAssets = await removeIncompleteDraftAssets(github, context, release, names);
        const existing = verifyAssetDigests(completedAssets, names);
        await uploadMissingAssets(github, context, release, names, existing);
    } else {
        const existing = verifyAssetDigests(release.assets, names);
        if (existing.size !== names.length) throw new Error('Published release has missing assets.');
    }

    // Confirm GitHub applied immutable-release settings and kept the tag on the approved source.
    const published = await findRelease(github, context, tag);
    if (!published?.immutable || published.draft || (await getTagCommit(github, context, tag)) !== revision) {
        throw new Error('The release must be immutable and its tag must identify the approved revision.');
    }
};

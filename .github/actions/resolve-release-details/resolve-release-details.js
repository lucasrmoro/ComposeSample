const { execFileSync } = require('node:child_process');

const LEGACY_BASELINE = 'fac9fc388387651aac858957d85107fb5bb43e51';
const INITIAL_RELEASE_VERSION = [1, 0, 0];
const TITLE_PATTERN = /^(feature|bugfix|hotfix|docs|ci|refactor|build|chore): (\S[^\r\n]*)$/;

function git(...args) {
    return execFileSync('git', args, { encoding: 'utf8' }).trim();
}

function commitTitle(sha) {
    return git('show', '-s', '--format=%s', sha);
}

function incrementVersion([major, minor, patch], title) {
    const match = TITLE_PATTERN.exec(title);
    if (!match) throw new Error('Use the validated PR title as the squash subject.');
    // A breaking title resets minor/patch; a feature increments minor; other accepted types increment patch.
    if (match[2].startsWith('[breaking] ')) return [major + 1, 0, 0];
    return match[1] === 'feature' ? [major, minor + 1, 0] : [major, minor, patch + 1];
}

function validateInput(revision, previewTitle, context) {
    const preview = context.eventName === 'pull_request';
    const releaseEvent = ['push', 'workflow_dispatch'].includes(context.eventName);

    // Preview runs calculate a version for the PR merge ref; real builds and retries must run on protected main.
    if (!preview && (
        !releaseEvent || context.ref !== 'refs/heads/main' || process.env.GITHUB_REF_PROTECTED !== 'true'
    )) {
        throw new Error('Android release builds require an update or recovery on protected main.');
    }
    if (preview ? revision !== 'HEAD' || !previewTitle : !/^[0-9a-f]{40}$/.test(revision) || previewTitle) {
        throw new Error('Expected an exact approved commit SHA or the PR merge ref.');
    }

    return preview;
}

async function verifyRevision(commit, preview, context, github) {
    const parent = git('rev-parse', `${commit}^1`);
    if (preview) {
        // The PR preview is valid only while its merge ref is based on the current main revision.
        const main = git('ls-remote', 'origin', 'refs/heads/main').split(/\s+/)[0];
        if (parent !== main) throw new Error('Update the PR from main and rerun validation.');
        return parent;
    }

    // A push or manual retry must point to the exact merge commit of a PR targeting main.
    if (!git('rev-list', '--first-parent', 'HEAD').split('\n').includes(commit)) {
        throw new Error('The release revision must be on main’s first-parent history.');
    }
    const { data: pulls } = await github.rest.repos.listPullRequestsAssociatedWithCommit({
        ...context.repo,
        commit_sha: commit
    });
    const merged = pulls.find(pr => pr.merged_at && pr.base.ref === 'main' && pr.merge_commit_sha === commit);
    if (!merged || TITLE_PATTERN.exec(commitTitle(commit))?.[1] !== merged.head.ref.split('/')[0]) {
        throw new Error('The revision must come from a merged PR with the matching squash subject.');
    }
    return parent;
}

function calculateRelease(parent, commit, previewTitle) {
    // Replay first-parent titles after the legacy baseline; the first accepted revision starts at 1.0.0.
    const history = git('rev-list', '--first-parent', parent).split('\n');
    const baselineIndex = history.indexOf(LEGACY_BASELINE);
    if (baselineIndex < 0) throw new Error('The legacy release baseline must be in main history.');

    const acceptedRevisions = history.slice(0, baselineIndex).reverse();
    const firstRelease = acceptedRevisions.length === 0;
    const previousVersion = acceptedRevisions.slice(1).reduce(
        (version, sha) => incrementVersion(version, commitTitle(sha)), INITIAL_RELEASE_VERSION
    );
    const previousTag = firstRelease ? '' : `v${previousVersion.join('.')}`;
    const version = firstRelease
        ? INITIAL_RELEASE_VERSION.join('.')
        : incrementVersion(previousVersion, previewTitle || commitTitle(commit)).join('.');
    // Android's legacy APK used code 1; add one for every accepted main revision after that baseline.
    const code = baselineIndex + 2;
    if (code > 2100000000) throw new Error('Android versionCode limit exceeded.');

    return { version, code, previousTag };
}

function verifyTag(tag, commit, preview) {
    const existingTag = git('tag', '--list', tag);
    if (existingTag && (preview || git('rev-parse', `${tag}^{commit}`) !== commit)) {
        throw new Error(`${tag} already belongs to a different release.`);
    }
}

module.exports = async ({ github, context, core }) => {
    const { REVISION: revision, PREVIEW_TITLE: previewTitle } = process.env;
    const preview = validateInput(revision, previewTitle, context);
    const commit = git('rev-parse', `${revision}^{commit}`);
    const parent = await verifyRevision(commit, preview, context, github);
    const { version, code, previousTag } = calculateRelease(parent, commit, previewTitle);
    const tag = `v${version}`;

    verifyTag(tag, commit, preview);
    for (const [key, value] of Object.entries({
        commit,
        parent,
        version,
        code,
        tag,
        previous_tag: previousTag
    })) {
        core.setOutput(key, value);
    }
    await core.summary.addRaw(`Android release version **${tag}** (versionCode ${code})\n`).write();
};

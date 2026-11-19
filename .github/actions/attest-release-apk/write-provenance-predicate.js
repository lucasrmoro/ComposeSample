const fs = require('node:fs');

const sourceCommit = process.env.SOURCE_COMMIT;
const versionCode = Number(process.env.VERSION_CODE);
if (!/^[0-9a-f]{40}$/.test(sourceCommit)) throw new Error('Expected the exact source commit SHA.');
if (!Number.isSafeInteger(versionCode) || versionCode < 1) throw new Error('Expected a valid Android version code.');
if (!['dev', 'prd'].includes(process.env.FLAVOR)) throw new Error('Unsupported release flavor.');

// The dispatch SHA identifies the workflow run; SOURCE_COMMIT identifies the possibly older source being retried.
const predicate = {
    sourceCommit,
    sourceRepository: process.env.GITHUB_REPOSITORY,
    release: {
        apkName: process.env.APK_NAME,
        flavor: process.env.FLAVOR,
        versionName: process.env.VERSION_NAME,
        versionCode
    },
    workflow: {
        path: '.github/workflows/android-release.yml',
        ref: process.env.GITHUB_REF,
        commit: process.env.GITHUB_SHA,
        runId: process.env.GITHUB_RUN_ID,
        runAttempt: process.env.GITHUB_RUN_ATTEMPT,
        event: process.env.GITHUB_EVENT_NAME
    }
};

fs.writeFileSync('release-predicate.json', `${JSON.stringify(predicate, null, 2)}\n`);

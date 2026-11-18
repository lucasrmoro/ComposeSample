const fs = require('node:fs');
const path = require('node:path');

const REPORT_MARKER = '<!-- pull-request-quality-gates-report -->';
const GATE_NAMES = { lint: 'Lint' };

function isRepositorySourceFile(annotation, workspace) {
    if (!annotation.path || !annotation.start_line) return false;

    // Ignore annotations in the CI configuration and reject paths that escape the checkout.
    const relativePath = path.posix.normalize(annotation.path);
    if (relativePath === '.github' || relativePath.startsWith('.github/')) return false;
    if (relativePath.startsWith('../') || path.posix.isAbsolute(relativePath)) return false;

    const sourcePath = path.join(workspace, relativePath);
    return fs.existsSync(sourcePath) && fs.statSync(sourcePath).isFile();
}

function formatViolation(gateName, annotation, context, owner, repo) {
    const location = `${annotation.path}#L${annotation.start_line}`;
    const fileUrl = `${context.serverUrl}/${owner}/${repo}/blob/${context.sha}/${location}`;
    const detail = (annotation.message || annotation.title || 'Check violation')
        .replace(/\r\n?|\n/g, '<br>')
        .replaceAll('|', '\\|');
    return `| ${gateName} | [${location}](${fileUrl}) | ${detail} |`;
}

async function findViolations(github, context, results) {
    const { owner, repo } = context.repo;
    const checkRuns = await github.paginate(github.rest.checks.listForRef, {
        owner,
        repo,
        ref: context.sha,
        per_page: 100
    });
    const violations = [];

    for (const [job, gateName] of Object.entries(GATE_NAMES)) {
        if (results[job]?.result !== 'failure') continue;
        // A check run can have many annotations, so paginate to avoid silently dropping later findings.
        const failedChecks = checkRuns.filter(check => check.name === gateName && check.conclusion === 'failure');
        for (const check of failedChecks) {
            const annotations = await github.paginate(github.rest.checks.listAnnotations, {
                owner,
                repo,
                check_run_id: check.id,
                per_page: 100
            });
            for (const annotation of annotations) {
                if (!isRepositorySourceFile(annotation, process.env.GITHUB_WORKSPACE)) continue;
                violations.push(formatViolation(gateName, annotation, context, owner, repo));
            }
        }
    }

    return violations;
}

function createReportBody(violations) {
    return [
        REPORT_MARKER,
        '## Quality gate violations',
        '',
        '| Gate | File and line | Violation |',
        '| --- | --- | --- |',
        ...violations,
        ''
    ].join('\n');
}

async function minimizePreviousReports(github, owner, repo, issueNumber, comments) {
    // Keep only one active bot report; older reports remain available in GitHub's minimized comments.
    const previousReports = comments.filter(comment =>
        comment.user?.type === 'Bot' && comment.body?.startsWith(REPORT_MARKER)
    );

    for (const comment of previousReports) {
        const status = await github.graphql(`
            query($subjectId: ID!) {
                node(id: $subjectId) {
                    ... on IssueComment { isMinimized }
                }
            }
        `, { subjectId: comment.node_id });
        if (status.node?.isMinimized) continue;

        await github.graphql(`
            mutation($subjectId: ID!) {
                minimizeComment(input: { subjectId: $subjectId, classifier: OUTDATED }) {
                    minimizedComment { isMinimized }
                }
            }
        `, { subjectId: comment.node_id });
    }
}

module.exports = async ({ github, context }) => {
    const results = JSON.parse(process.env.GATE_RESULTS_JSON);
    const violations = await findViolations(github, context, results);
    if (violations.length === 0) return;

    const { owner, repo } = context.repo;
    const issueNumber = context.issue.number;
    const comments = await github.paginate(github.rest.issues.listComments, {
        owner,
        repo,
        issue_number: issueNumber
    });

    await github.rest.issues.createComment({
        owner,
        repo,
        issue_number: issueNumber,
        body: createReportBody(violations)
    });
    await minimizePreviousReports(github, owner, repo, issueNumber, comments);
};

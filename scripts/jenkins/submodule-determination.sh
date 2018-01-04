# Determine the files that have changed for this build
#
# For master builds, it finds all files changed
# since the last successful build
#
# For PR builds, it finds all files changed in every commit
# that is a part of the PR branch.
#
# For builds that specify a branch, it finds all files changed
# in every commit that is a part of the PR branch.

scriptDir="$(dirname "$0")"

if [[ ("$GIT_BRANCH" == "origin/master") ]]
then
  # This block is for master builds
  IS_MASTER_BUILD=true
  firstCommit=$GIT_PREVIOUS_SUCCESSFUL_COMMIT
  lastCommit=$GIT_COMMIT
elif [ -z ${ghprbPullId+x} ] # if no ghprbPullId
then
  # So ghprbPullId is NOT set, so...
  # this block assumes a build pointed at a specific branch, like 'feature/do-something-cool'
  # most likely on an individual user's github, for pre-PR testing purposes.
  firstCommit=$(git log --no-merges $GIT_BRANCH --not origin/master --format=format:%H | tail -n 1)~1
  lastCommit=$GIT_COMMIT
else
  # ghprbPullId IS set, so this block is for PR builds
  git config --add remote.origin.fetch +refs/pull/*/head:refs/remotes/origin/pull/*
  git fetch origin
  # Note the ~1 at the end. It is a diff, so we want the difference between the
  # commit before the first commit in the branch and the current commit
  firstCommit=$(git log --no-merges origin/pull/$ghprbPullId --not origin/master --format=format:%H | tail -n 1)~1
  lastCommit=$ghprbActualCommit
fi

# 1) only want names,
# 2) need to turn off rename detection which was enabled by default
# in git 2.9. If rename detection is on, the only file reported
# is the new destination not the old one.
files=$(git diff --name-only --no-renames $firstCommit $lastCommit)

echo "*** BEGIN FILES CHANGED"
echo $files | tr " " "\n"
echo "*** END FILES CHANGED"

submodulesToTest=$(node $scriptDir/../node/determine-apps-to-test.js $files)
checkError "Error occurred attempting to build list of submodules to test"

echo "*** BEGIN SUBMODULES TO TEST"
echo $submodulesToTest | tr " " "\n"
echo "*** END SUBMODULES TO TEST"

echo "SUBMODULES_TO_TEST='$submodulesToTest'" > submodules.txt
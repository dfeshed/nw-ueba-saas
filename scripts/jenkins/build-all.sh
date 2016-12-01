#### Run Entire Build
#
# 1) Will ensure node/npm, yarn, bower and ember-cli are
# installed and with the correct version
# 2) Will then run builds for the 3 ember apps
#   which includes tests and production builds
#   and re-hosting of style-guide
# 3) Builds RPM for SA
# 4) Puts RPM into YUM repos
#

# For testing purposes
# temp clean up while setting up script
# rm -rf ~/.node

# turns off noisy jenkins output
# possibly comment this out to debug problematic build
set +x

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

if [[ ("$GIT_BRANCH" == "origin/master") ]]
then
  # This block is for master builds
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

# bring in utilities and settings
scriptDir="$(dirname "$0")"
. $scriptDir/../_util.sh
. $scriptDir/_settings.sh

echo "*** BEGIN FILES CHANGED"
echo $files | tr " " "\n"
echo "*** END FILES CHANGED"

submodulesToBuild=$(node $scriptDir/../node/determine-apps-to-build.js $files)
# not breaking build for this...yet
# checkError "Error occurred attempting to build list of submodules to build"

echo "*** BEGIN SUBODULES TO BUILD"
echo $submodulesToBuild | tr " " "\n"
echo "*** END SUBODULES TO BUILD"

if [ -z ${EXTENT+x} ]
then
  fail "Please export EXTENT variable:"
  fail "export EXTENT=FULL"
  fail "or"
  fail "export EXTENT=RPM"
  fail "or"
  fail "export EXTENT=TEST"
  exit 1
fi

#### Setup environment ####
. $scriptDir/environment.sh

#### Build Apps ####
. $scriptDir/apps.sh

if [[ "$EXTENT" == "FULL" || "$EXTENT" == "RPM" ]]
then
  #### Build RPM if running full or rpm build
  . $scriptDir/rpm.sh
fi

if [ "$EXTENT" == "FULL" ]
then
  #### Moving RPM to Yum directory if running full build
  . $scriptDir/yum.sh
fi

#### Victory, poppin bottles
success "Finished Build!"
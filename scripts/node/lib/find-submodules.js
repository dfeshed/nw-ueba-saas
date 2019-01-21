const path = require('path');
const ALL_SUBMODULES = require('./submodule-config').all;

const buildUniqueList = (things) => {
  return Object.keys(things.reduce((accum, thing) => {
    accum[thing] = undefined;
    return accum;
  }, {}));
};

// files that when changed should not affect
// what modules are built, just toss them out
const BLACKLIST = [

  // Because our translations are centralized in component-lib
  // and because component-lib is used by everything, anytime
  // someone changes component-lib to just update a translation
  // key, most of the modules are built. For now, avoid this,
  // see how that goes.
  //
  // Down side? If some test depends on specific text, and that
  // text changes, and the module the test is in does not,
  // the test will not fail and will instead trip up the next
  // build that does change the module the test is in. Tradeoffs!
  // Thankfully changing some text in a test isn't a huge burden,
  // but it will slow someone's PR process down.
  'component-lib/addon/locales/en-us/trans-data.js',

  // nested docs
  'mock-server/README.md',
  'streaming-data/README.md',

  // root level documentation
  'README.md',
  'CHANGELOG.md',
  'CONTRIBUTING.md',
  'PULL_REQUEST_TEMPLATE.md'
];

const submodulesAffected = (submoduleList) => {

  // Get all files passed in
  let files = process.argv.splice(2);

  files = files.filter((file) => {
    if (BLACKLIST.includes(file)) {
      // uncomment to debug
      // console.log(`Blacklisted file will not affect build`, file);
      return false;
    }

    // temporarily filter out any `direct-access` files as they have been removed
    // and no entry in the `changedSubmodules` list should be created
    // this block will be removed in a PR following the merge of this PR
    if (file.startsWith('direct-access')) {
      return false;
    }

    return true;
  });

  // If no files changed, just do a vanilla sa build
  if (files.length === 0) {
    return submoduleList.sa.concat('sa');
  }

  // Any files that are at the root of the project?
  const rootFiles = files.filter((file) => file.indexOf(path.sep) === -1);

  // If a root file (like .eslintrc) has changed (infrequent),
  // for now lets just assume we have to build everything
  if (rootFiles.length > 0) {
    return buildUniqueList(ALL_SUBMODULES);
  }

  // yank the first part of the path out, that is the app/submodule
  const roots = files.map((file) => file.split(path.sep)[0]);

  // generate unique list of app/submodule
  const changedSubmodules = buildUniqueList(roots);

  // Build list of apps to build based on above configuration
  // start with the list of things actually changed, then
  // append the dependents for those changed things
  let appsToBuild = changedSubmodules;
  changedSubmodules.forEach((app) => {
    if (!submoduleList[app]) {
      console.error('submodule unaccounted for in the build configuration!', app);
      process.exit(1);
    }
    appsToBuild = appsToBuild.concat(submoduleList[app]);
  });

  // Make that unique too
  const appsToBuildUnique = buildUniqueList(appsToBuild);

  return appsToBuildUnique;
};

module.exports = submodulesAffected;
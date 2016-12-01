const path = require('path');

const ALL_SUBMODULES = [
  'component-lib',
  'mock-server',
  'recon',
  'sa',
  'streaming-data',
  'style-guide'
];

// A configuration that indicates dependents
// for each sa sub-module/directory
const saModuleDependents = {
  'component-lib': [
    'recon',
    'sa',
    'style-guide'
  ],
  docs: [], // lol
  'mock-server': [ // This list will grow as more uses the mock-server
    'streaming-data',
    'recon'
  ],
  recon: [
    'sa'
  ],
  sa: [], // sa is an end state app, nothing depends on it
  scripts: ALL_SUBMODULES, // Everything depends on scripts
  'streaming-data': [
    'recon',
    'sa',
    'style-guide'
  ],
  'style-guide': [] // style-guide is an end state app, nothing depends on it
};

const buildUniqueList = (things) => {
  return Object.keys(things.reduce((accum, thing) => {
    accum[thing] = undefined;
    return accum;
  }, {}));
};

// Get all files passed in
const files = process.argv.splice(2);

// Any files that are at the root of the project?
const rootFiles = files.filter((file) => file.indexOf(path.sep) === -1);

// If a root file (like .eslintrc) has changed (infrequent),
// for now lets just assume we have to build everything
if (rootFiles.length > 0) {
  console.log(ALL_SUBMODULES.join(' '));
  process.exit(0);
}

// yank the first part of the path out, that is the app/submodule
const roots = files.map((file) => file.split(path.sep)[0]);

// generate unique list of app/submodule
const changedSubmodules = buildUniqueList(roots);

// Build list of apps to build based on above configuration
// start with the list of things actually changed, then
// append the dependents for those changed things
var appsToBuild = changedSubmodules;
changedSubmodules.forEach((app) => {
  if (!saModuleDependents[app]) {
    console.error('submodule unaccounted for in the build configuration!', app);
    process.exit(1);
  }
  appsToBuild = appsToBuild.concat(saModuleDependents[app]);
});

// Make that unique too
const appsToBuildUnique = buildUniqueList(appsToBuild);

// console.log it, this essentially passes it
// to the bash script that called it
console.log(appsToBuildUnique.join(' '));
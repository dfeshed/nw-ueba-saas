const ALL_SUBMODULES = [
  'component-lib',
  'mock-server',
  'recon',
  'sa',
  'streaming-data',
  'style-guide'
];

// A configuration that lists each submodule with
// an array of that submodules dependants, that is,
// the other submodules that depend upon it.
const saModuleDependants = {
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

// A configuration that lists each submodule with
// an array of that submodules dependencies, that is,
// the other submodules that it uses.
const saModuleDependencies = {
  'component-lib': [],
  docs: [], // lol
  'mock-server': [],
  recon: [
    'component-lib',
    'streaming-data',
    'mock-server'
  ],
  sa: [
    'component-lib',
    'streaming-data',
    'recon'
  ],
  scripts: ALL_SUBMODULES,
  'streaming-data': [
    'mock-server'
  ],
  'style-guide': [
    'component-lib',
    'streaming-data'
  ]
};

// walk the dependency tree
const buildDependencies = (mod) => {
  const nestedDeps = saModuleDependencies[mod];
  saModuleDependants[mod].forEach((dep) => {
    nestedDeps.push.apply(nestedDeps, buildDependencies(dep));
  });
  return nestedDeps;
};

// Create the toInstallConfig, a config which indicates what
// submodules need to be installed for each module. That is the
// combination of:
//
// 1) dependencies required by a module
//    For example, 'recon'' changed, so 'streaming-data'' must be installed
//    in order to run 'recon'' tests.
// 2) dependants
//    For example, 'recon'' changed, so 'sa' must be installed
//    so that it can be tested as it depends on 'recon'.
// 3) and the dependants full dependency trees
//    For example, 'mock-server' changed, and 'recon' depends on it so it
//    must be tested, which means streaming-data must also be installed
//    as it is a dependency of 'recon'
const toInstallConfig = {};
Object.keys(saModuleDependencies).forEach((k) => {
  // start with this module's dependencies
  var merged = saModuleDependencies[k];

  // then include those that are dependants
  merged = merged.concat(saModuleDependants[k]);

  // then include those that are dependencies of the dependants
  merged = merged.concat(buildDependencies(k));

  // unique-ify
  merged = merged.filter((item, i) => merged.indexOf(item) == i);

  toInstallConfig[k] = merged;
});

module.exports = {
  all: ALL_SUBMODULES,
  toTestConfig: saModuleDependants,  // the toTestConfig is just the list of dependants
  toInstallConfig: toInstallConfig
};
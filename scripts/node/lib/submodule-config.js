const ALL_SUBMODULES = [
  'component-lib',
  'mock-server',
  'packager',
  'recon',
  'investigate-events',
  'investigate-hosts',
  'investigate-files',
  'respond',
  'sa',
  'streaming-data',
  'style-guide',
  'context',
  'preferences',
  'test-helpers'
];

// A configuration that lists each submodule with
// an array of that submodules dependants, that is,
// the other submodules that depend upon it.
const saModuleDependants = {
  'component-lib': [
    'recon',
    'sa',
    'style-guide',
    'investigate-events',
    'investigate-hosts',
    'investigate-files',
    'packager',
    'respond',
    'context',
    'preferences'
  ],
  docs: [], // lol
  'mock-server': [
    'streaming-data',
    'recon',
    'investigate-events',
    'investigate-hosts',
    'investigate-files',
    'packager',
    'respond',
    'context'
  ],
  'investigate-events': ['sa'],
  'investigate-hosts': ['sa'],
  'investigate-files': ['sa'],
  packager: ['sa'],
  recon: [
    'sa',
    'investigate-events'
  ],
  sa: [], // sa is an end state app, nothing depends on it
  respond: ['sa'],
  scripts: ALL_SUBMODULES, // Everything depends on scripts
  'streaming-data': [
    'recon',
    'investigate-events',
    'investigate-hosts',
    'investigate-files',
    'packager',
    'respond',
    'sa',
    'style-guide',
    'context'
  ],
  'style-guide': [], // style-guide is an end state app, nothing depends on it
  context: [
    'sa',
    'respond'
  ],
  preferences: [
    'sa',
    'investigate-events'
  ],
  'test-helpers': []
};

module.exports = {
  all: ALL_SUBMODULES,
  toTestConfig: saModuleDependants
};

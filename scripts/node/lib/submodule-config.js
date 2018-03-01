const ALL_SUBMODULES = [
  'component-lib',
  'mock-server',
  'packager',
  'recon',
  'investigate-shared',
  'investigate-events',
  'investigate-hosts',
  'investigate-files',
  'respond',
  'configure',
  'sa',
  'streaming-data',
  'style-guide',
  'context',
  'preferences',
  'test-helpers',
  'ember-route-action-helper',
  'hosts-scan-configure'
];

// A configuration that lists each submodule with
// an array of that submodules dependants, that is,
// the other submodules that depend upon it.
const saModuleDependants = {
  'component-lib': [
    'recon',
    'sa',
    'style-guide',
    'investigate-shared',
    'investigate-events',
    'investigate-hosts',
    'investigate-files',
    'packager',
    'respond',
    'configure',
    'context',
    'preferences',
    'hosts-scan-configure'
  ],
  docs: [], // lol
  'mock-server': [
    'streaming-data',
    'recon',
    'investigate-shared',
    'investigate-events',
    'investigate-hosts',
    'investigate-files',
    'packager',
    'respond',
    'configure',
    'context',
    'hosts-scan-configure'
  ],
  'investigate-shared': [
    'investigate-events',
    'investigate-hosts',
    'investigate-files'
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
  configure: ['sa'],
  scripts: ALL_SUBMODULES, // Everything depends on scripts
  'streaming-data': [
    'recon',
    'investigate-shared',
    'investigate-events',
    'investigate-hosts',
    'investigate-files',
    'packager',
    'respond',
    'configure',
    'sa',
    'style-guide',
    'context',
    'hosts-scan-configure'
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
  'test-helpers': [],
  'ember-route-action-helper': [],
  'hosts-scan-configure': ['sa', 'configure']
};

module.exports = {
  all: ALL_SUBMODULES,
  toTestConfig: saModuleDependants
};

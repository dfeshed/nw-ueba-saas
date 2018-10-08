const ALL_SUBMODULES = [
  'component-lib',
  'mock-server',
  'packager',
  'recon',
  'investigate-shared',
  'investigate-events',
  'investigate-hosts',
  'investigate-files',
  'entity-details',
  'investigate-users',
  'investigate',
  'respond-shared',
  'respond',
  'configure',
  'admin',
  'admin-source-management',
  'sa',
  'streaming-data',
  'style-guide',
  'context',
  'preferences',
  'test-helpers',
  'ember-route-action-helper',
  'ember-cli-document-title',
  'investigate-process-analysis',
  'rsa-context-menu',
  'rsa-data-filters',
  'direct-access'
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
    'entity-details',
    'investigate-users',
    'packager',
    'respond-shared',
    'respond',
    'configure',
    'admin',
    'admin-source-management',
    'context',
    'preferences',
    'investigate-process-analysis',
    'rsa-context-menu',
    'rsa-data-filters',
    'direct-access'
  ],
  docs: [], // lol
  'mock-server': [
    'streaming-data',
    'recon',
    'investigate-shared',
    'investigate-events',
    'investigate-hosts',
    'investigate-files',
    'entity-details',
    'investigate-users',
    'investigate',
    'packager',
    'respond',
    'configure',
    'admin',
    'admin-source-management',
    'context',
    'investigate-process-analysis',
    'rsa-context-menu'
  ],
  'investigate-shared': [
    'investigate-events',
    'investigate-hosts',
    'investigate-files',
    'entity-details',
    'investigate-users',
    'investigate-process-analysis',
    'sa'
  ],
  'investigate-events': ['sa'],
  'investigate-hosts': ['sa'],
  'investigate-files': ['sa'],
  'entity-details': ['sa'],
  'investigate-users': ['sa'],
  'investigate-process-analysis': ['sa'],
  packager: ['sa'],
  recon: [
    'sa',
    'investigate-events'
  ],
  sa: [], // sa is an end state app, nothing depends on it
  'respond-shared': [
    'respond',
    'sa'
  ],
  respond: ['sa'],
  configure: ['sa'],
  investigate: ['sa'],
  admin: ['sa'],
  'admin-source-management': ['sa', 'admin'],
  scripts: ALL_SUBMODULES, // Everything depends on scripts
  'streaming-data': [
    'recon',
    'investigate-shared',
    'investigate-events',
    'investigate-hosts',
    'investigate-files',
    'entity-details',
    'investigate-users',
    'packager',
    'respond',
    'configure',
    'admin',
    'admin-source-management',
    'sa',
    'style-guide',
    'context',
    'investigate-process-analysis'
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
  'rsa-context-menu': [
    'sa',
    'investigate-events',
    'investigate-hosts',
    'recon',
    'style-guide'
  ],
  'test-helpers': [],
  'ember-route-action-helper': [],
  'rsa-data-filters': [
    'sa',
    'investigate-hosts',
    'investigate-files'
  ],
  'ember-cli-document-title': [],
  'hosts-scan-configure': ['sa', 'configure'],
  'direct-access': []
};

module.exports = {
  all: ALL_SUBMODULES,
  toTestConfig: saModuleDependants
};

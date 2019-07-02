const ALL_SUBMODULES = [
  'component-lib',
  'mock-server',
  'packager',
  'ember-simple-auth',
  'endpoint-rar',
  'rsa-dashboard',
  'recon',
  'investigate-shared',
  'investigate-events',
  'investigate-hosts',
  'investigate-files',
  'entity-details',
  'investigate-users',
  'investigate',
  'respond-shared',
  'rsa-list-manager',
  'respond',
  'configure',
  'admin',
  'admin-source-management',
  'sa',
  'streaming-data',
  'style-guide',
  'context',
  'preferences',
  'license',
  'test-helpers',
  'investigate-process-analysis',
  'rsa-context-menu',
  'rsa-data-filters',
  'ngcoreui'
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
    'endpoint-rar',
    'rsa-dashboard',
    'respond-shared',
    'rsa-list-manager',
    'respond',
    'configure',
    'admin',
    'admin-source-management',
    'context',
    'preferences',
    'license',
    'investigate-process-analysis',
    'rsa-context-menu',
    'rsa-data-filters',
    'ngcoreui'
  ],
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
    'endpoint-rar',
    'rsa-dashboard',
    'license',
    'respond',
    'configure',
    'admin',
    'admin-source-management',
    'context',
    'investigate-process-analysis',
    'rsa-list-manager',
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
  'endpoint-rar': ['sa'],
  'rsa-dashboard': [
    'investigate-hosts',
    'investigate-files',
    'investigate-users',
    'sa'
  ],
  recon: [
    'sa',
    'investigate-events'
  ],
  sa: [], // sa is an end state app, nothing depends on it
  'respond-shared': [
    'respond',
    'sa'
  ],
  'rsa-list-manager': [
    'investigate-events',
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
    'endpoint-rar',
    'rsa-dashboard',
    'license',
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
    'respond',
    'respond-shared',
    'rsa-list-manager',
    'investigate',
    'investigate-events',
    'investigate-files',
    'investigate-shared',
    'investigate-hosts'
  ],
  preferences: [
    'sa',
    'investigate-events'
  ],
  license: [
    'sa',
    'investigate',
    'investigate-shared',
    'respond',
    'admin',
    'configure'
  ],
  'rsa-context-menu': [
    'sa',
    'investigate-events',
    'investigate-hosts',
    'recon',
    'style-guide'
  ],
  'rsa-data-filters': [
    'sa',
    'investigate-hosts',
    'investigate-files'
  ],
  'hosts-scan-configure': ['sa', 'configure'],
  'ngcoreui': [],
  'test-helpers': [],
  'ember-computed-decorators': [],
  'ember-simple-auth': [],
  docs: [] // lol
};

module.exports = {
  all: ALL_SUBMODULES,
  toTestConfig: saModuleDependants
};

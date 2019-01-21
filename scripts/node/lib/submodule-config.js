const ALL_SUBMODULES = [
  'component-lib',
  'mock-server',
  'packager',
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
  'ember-cli-document-title',
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
    'rsa-dashboard',
    'respond-shared',
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
    'rsa-dashboard',
    'license',
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
    'respond-shared'
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
  'test-helpers': [],
  'rsa-data-filters': [
    'sa',
    'investigate-hosts',
    'investigate-files'
  ],
  'ember-cli-document-title': [],
  'hosts-scan-configure': ['sa', 'configure'],
  'ngcoreui': [],
  'ember-computed-decorators': []
};

module.exports = {
  all: ALL_SUBMODULES,
  toTestConfig: saModuleDependants
};

import Application from 'ember-application';
import Resolver from './resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

const App = Application.extend({
  modulePrefix: config.modulePrefix,
  podModulePrefix: config.podModulePrefix,
  Resolver,

  engines: {
    admin: {
      dependencies: {
        services: [
          '-document',
          'access-control',
          'contextual-help',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus',
          'router',
          'app-version',
          'features'
        ],
        externalRoutes: {
          protected: 'protected',
          'admin.admin-source-management': 'protected.admin.admin-source-management'
        }
      }
    },
    adminSourceManagement: {
      dependencies: {
        services: [
          '-document',
          'access-control',
          'contextual-help',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus',
          'router',
          'app-version',
          'features'
        ],
        externalRoutes: {
          protected: 'protected'
          // 'admin.admin-other-engine-2': 'protected.admin.admin-other-engine-2',
          // 'admin.admin-other-engine-3': 'protected.admin.admin-other-engine-3'
        }
      }
    },
    respond: {
      dependencies: {
        services: [
          '-document',
          'access-control',
          'contextual-help',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus'
        ],
        externalRoutes: {
          protected: 'protected'
        }
      }
    },
    configure: {
      dependencies: {
        services: [
          '-document',
          'access-control',
          'contextual-help',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus',
          'router'
        ],
        externalRoutes: {
          protected: 'protected'
        }
      }
    },
    investigate: {
      dependencies: {
        services: [
          '-document',
          'access-control',
          'contextual-help',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus',
          'app-version',
          'router',
          'features'
        ],
        externalRoutes: {
          'investigate.investigate-files': 'protected.investigate.investigate-files',
          'investigate.investigate-events': 'protected.investigate.investigate-events',
          'investigate.investigate-hosts': 'protected.investigate.investigate-hosts'
        }
      }
    },
    investigateEvents: {
      dependencies: {
        services: [
          '-document',
          'access-control',
          'contextual-help',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus',
          'router',
          'app-version'
        ],
        externalRoutes: {
          'investigate.investigate-files': 'protected.investigate.investigate-files',
          'investigate.investigate-hosts': 'protected.investigate.investigate-hosts'
        }
      }
    },
    investigateFiles: {
      dependencies: {
        services: [
          '-document',
          'access-control',
          'contextual-help',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus',
          'features'
        ],
        externalRoutes: {
          'investigate.investigate-events': 'protected.investigate.investigate-events',
          'investigate.investigate-hosts': 'protected.investigate.investigate-hosts'
        }
      }
    },
    investigateHosts: {
      dependencies: {
        services: [
          '-document',
          'access-control',
          'contextual-help',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus',
          'features'
        ],
        externalRoutes: {
          'investigate.investigate-files': 'protected.investigate.investigate-files',
          'investigate.investigate-events': 'protected.investigate.investigate-events'
        }
      }
    },
    investigateProcessAnalysis: {
      dependencies: {
        services: [
          '-document',
          'access-control',
          'contextual-help',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus',
          'router',
          'features'
        ]
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

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
          'features',
          'global-preferences'
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
          'features',
          'global-preferences'
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
          'eventBus',
          'global-preferences'
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
          'router',
          'global-preferences'
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
          'features',
          'investigatePage',
          'global-preferences'
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
          'app-version',
          'global-preferences'
        ],
        externalRoutes: {
          'investigate.investigate-files': 'protected.investigate.investigate-files',
          'investigate.investigate-hosts': 'protected.investigate.investigate-hosts',
          'investigate.investigate-users': 'protected.investigate.investigate-users'
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
          'global-preferences'
        ],
        externalRoutes: {
          'investigate.investigate-events': 'protected.investigate.investigate-events',
          'investigate.investigate-hosts': 'protected.investigate.investigate-hosts',
          'investigate.investigate-users': 'protected.investigate.investigate-users'
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
          'global-preferences'
        ],
        externalRoutes: {
          'investigate.investigate-files': 'protected.investigate.investigate-files',
          'investigate.investigate-events': 'protected.investigate.investigate-events',
          'investigate.investigate-users': 'protected.investigate.investigate-users'
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
          'global-preferences'
        ]
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

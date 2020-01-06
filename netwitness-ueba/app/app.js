import Application from 'ember-application';
import Resolver from './resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

const App = Application.extend({
  modulePrefix: config.modulePrefix,
  podModulePrefix: config.podModulePrefix,
  Resolver,
  customEvents: {
    paste: 'paste',
    cut: 'cut'
  },
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
          'global-preferences',
          'session'
        ],
        externalRoutes: {
          protected: 'protected',
          'admin.admin-source-management': 'protected.admin.admin-source-management'
        }
      }
    },
    investigateUsers: {
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
          'investigatePage'
        ],
        externalRoutes: {
          'investigate.investigate-files': 'protected.investigate.investigate-files',
          'investigate.investigate-hosts': 'protected.investigate.investigate-hosts',
          'investigate.investigate-events': 'protected.investigate.investigate-events'
        }
      }
    },
    entityDetails: {
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
          'investigate.investigate-hosts': 'protected.investigate.investigate-hosts',
          'investigate.investigate-events': 'protected.investigate.investigate-events',
          'investigate.investigate-users': 'protected.investigate.investigate-users'
        }
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

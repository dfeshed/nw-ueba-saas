import Application from 'ember-application';
import Resolver from './resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

const App = Application.extend({
  modulePrefix: config.modulePrefix,
  podModulePrefix: config.podModulePrefix,
  Resolver,

  engines: {
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
          'flashMessages'
        ],
        externalRoutes: {
          protected: 'protected',
          'protected.investigate.investigate-files': 'protected.investigate.investigate-files'
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
    investigateFiles: {
      dependencies: {
        services: [
          '-document',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus'
        ],
        externalRoutes: {
          protected: 'protected',
          'protected.investigate.investigate-events': 'protected.investigate.investigate-events'
        }
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

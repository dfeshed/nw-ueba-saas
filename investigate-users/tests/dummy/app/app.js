import Application from '@ember/application';
import Resolver from './resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

const App = Application.extend({
  modulePrefix: config.modulePrefix,
  podModulePrefix: config.podModulePrefix,
  Resolver,
  engines: {
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
          'eventBus',
          'flashMessages'
        ],
        externalRoutes: {
          'investigate.investigate-events': 'events',
          'investigate.investigate-hosts': 'hosts',
          'investigate.investigate-users': 'users',
          'investigate.investigate-files': 'files'
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
          'eventBus',
          'flashMessages'
        ]
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

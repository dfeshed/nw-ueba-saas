import Application from '@ember/application';
import Resolver from './resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

const App = Application.extend({
  modulePrefix: config.modulePrefix,
  podModulePrefix: config.podModulePrefix,
  Resolver,
  engines: {
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
          'eventBus',
          'flashMessages',
          'features'
        ],
        externalRoutes: {
          'investigate.investigate-events': 'events',
          'investigate.investigate-hosts': 'hosts',
          'investigate.investigate-users': 'users'
        }
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

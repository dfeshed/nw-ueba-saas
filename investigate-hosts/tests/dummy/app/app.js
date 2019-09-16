import Application from '@ember/application';
import Resolver from './resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

const App = Application.extend({
  modulePrefix: config.modulePrefix,
  podModulePrefix: config.podModulePrefix,
  Resolver,
  engines: {
    investigateHosts: {
      dependencies: {
        services: [
          '-document',
          'dateFormat',
          'contextual-help',
          'access-control',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'router',
          'eventBus',
          'investigatePage'
        ],
        externalRoutes: {
          'investigate.investigate-events': 'events',
          'investigate.investigate-files': 'files',
          'investigate.investigate-users': 'users'
        }
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

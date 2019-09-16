import Application from '@ember/application';
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
          'global-preferences',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus',
          'investigatePage'
        ],
        externalRoutes: {
          'investigate.investigate-files': 'files',
          'investigate.investigate-hosts': 'hosts',
          'investigate.investigate-users': 'users'
        }
      }
    }
  },
  customEvents: {
    paste: 'paste',
    cut: 'cut'
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

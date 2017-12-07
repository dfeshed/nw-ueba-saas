import Ember from 'ember';
import Resolver from './resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

const { Application } = Ember;

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
          'eventBus'
        ],
        externalRoutes: {
          protected: 'protected',
          'protected.investigate.investigate-events': 'events',
          'protected.investigate.investigate-files': 'files'
        }
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

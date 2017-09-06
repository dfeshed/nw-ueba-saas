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
          protected: 'protected'
        }
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

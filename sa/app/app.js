import Application from 'ember-application';
import Ember from 'ember';
import Resolver from './resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

Ember.MODEL_FACTORY_INJECTIONS = true;

const App = Application.extend({
  modulePrefix: config.modulePrefix,
  podModulePrefix: config.podModulePrefix,
  Resolver,

  engines: {
    investigate: {
      dependencies: {
        services: [
          '-document',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n'
        ]
      }
    },
    respond: {
      dependencies: {
        services: [
          '-document',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n',
          'flashMessages',
          'eventBus'
        ]
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

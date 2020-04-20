import Application from '@ember/application';
import Resolver from './resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

export default class App extends Application {
  modulePrefix = config.modulePrefix;
  podModulePrefix = config.podModulePrefix;
  Resolver = Resolver;
  engines = {
    springboard: {
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
          'session'
        ],
        externalRoutes: {
          protected: 'protected'
        }
      }
    }
  }
}

loadInitializers(App, config.modulePrefix);


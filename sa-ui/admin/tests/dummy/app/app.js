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
          'session'
        ],
        externalRoutes: {
          protected: 'protected',
          'admin.admin-source-management': 'admin.admin-source-management'
          // 'admin.admin-other-engine-2': 'admin.admin-other-engine-2',
          // 'admin.admin-other-engine-3': 'admin.admin-other-engine-3'
        }
      }
    },
    adminSourceManagement: {
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
          // 'admin.admin-other-engine-2': 'admin.admin-other-engine-2',
          // 'admin.admin-other-engine-3': 'admin.admin-other-engine-3'
        }
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

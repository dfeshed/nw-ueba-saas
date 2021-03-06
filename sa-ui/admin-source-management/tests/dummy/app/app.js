import Application from '@ember/application';
import Resolver from './resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

const App = Application.extend({
  modulePrefix: config.modulePrefix,
  podModulePrefix: config.podModulePrefix,
  Resolver,
  engines: {
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
          // 'admin.admin-other-engine-2': 'engine2PrettyPath',
          // 'admin.admin-other-engine-3': 'engine3PrettyPath'
        }
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

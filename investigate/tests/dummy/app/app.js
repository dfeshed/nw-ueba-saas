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
    investigate: {
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
          'app-version'
        ],
        externalRoutes: {
          'investigate.investigate-files': 'investigate.investigate-files',
          'investigate.investigate-events': 'investigate.investigate-events',
          'investigate.investigate-hosts': 'investigate.investigate-hosts'
        }
      }
    },
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
          'flashMessages',
          'eventBus',
          'router',
          'app-version'
        ],
        externalRoutes: {
          'investigate.investigate-files': 'investigate.investigate-files',
          'investigate.investigate-hosts': 'investigate.investigate-hosts'
        }
      }
    },
    investigateHosts: {
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
          'router'
        ],
        externalRoutes: {
          'investigate.investigate-files': 'investigate.investigate-files',
          'investigate.investigate-events': 'investigate.investigate-events'
        }
      }
    },
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
          'flashMessages',
          'eventBus'
        ],
        externalRoutes: {
          'investigate.investigate-hosts': 'investigate.investigate-hosts',
          'investigate.investigate-events': 'investigate.investigate-events'
        }
      }
    },
    investigateProcessAnalysis: {
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
          'router'
        ]
      }
    }
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

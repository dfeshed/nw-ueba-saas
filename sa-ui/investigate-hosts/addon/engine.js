import Engine from 'ember-engines/engine';
import Resolver from 'ember-resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

const { modulePrefix } = config;
const Eng = Engine.extend({
  modulePrefix,
  Resolver,

  dependencies: {
    services: [
      '-document',
      'access-control',
      // Used to update the app header's help link based on state changes
      'contextual-help',
      'dateFormat',
      'timeFormat',
      'timezone',
      'i18n',
      'flashMessages',
      'eventBus',
      'investigatePage'
    ],
    externalRoutes: [
      'investigate.investigate-files',
      'investigate.investigate-events',
      'investigate.investigate-users'
    ]
  }
});

loadInitializers(Eng, modulePrefix);

export default Eng;

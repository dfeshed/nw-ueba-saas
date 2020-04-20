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
      'eventBus',
      'flashMessages',
      'investigatePage'
    ],
    externalRoutes: [
      'investigate.investigate-events',
      'investigate.investigate-hosts',
      'investigate.investigate-files'
    ]
  }
});

loadInitializers(Eng, modulePrefix);

export default Eng;

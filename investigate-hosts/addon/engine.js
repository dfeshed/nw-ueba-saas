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
      'dateFormat',
      'timeFormat',
      'timezone',
      'i18n',
      'flashMessages',
      'eventBus'
    ],
    externalRoutes: [
      'protected',
      'protected.investigate.investigate-files',
      'protected.investigate.investigate-events'
    ]
  }
});

loadInitializers(Eng, modulePrefix);

export default Eng;

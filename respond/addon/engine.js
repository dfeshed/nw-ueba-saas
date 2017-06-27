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
      // this is shared to solve problems with ember-wormhole
      // https://github.com/yapplabs/ember-wormhole/issues/84
      '-document',

      // the following services are shared because they contain state
      // persisted by the parent app. If they are not shared, that state
      // is missing as the engine gets fresh copies of the service w/o state
      'access-control',
      'contextual-help',
      'dateFormat',
      'timeFormat',
      'timezone',
      'i18n',
      'flashMessages',
      'eventBus'
    ],
    externalRoutes: [
      'protected'
    ]
  }
});

loadInitializers(Eng, modulePrefix);

export default Eng;

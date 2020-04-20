import Engine from 'ember-engines/engine';
import loadInitializers from 'ember-load-initializers';
import Resolver from 'ember-resolver';
import config from './config/environment';

const { modulePrefix } = config;

const Eng = Engine.extend({
  modulePrefix,
  Resolver,

  init() {
    this._super(arguments);
    this.dependencies = this.dependencies || {
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
        'eventBus',
        'router',
        'app-version',
        'investigatePage'
      ],
      externalRoutes: [
        'investigate.investigate-files',
        'investigate.investigate-events',
        'investigate.investigate-users',
        'investigate.investigate-hosts'
      ]
    };
  }
});

loadInitializers(Eng, modulePrefix);

export default Eng;

import Ember from 'ember';
import Resolver from 'ember-resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

const {
  Application,
  $
} = Ember;

let App;

Ember.MODEL_FACTORY_INJECTIONS = true;

App = Application.extend({
  modulePrefix: config.modulePrefix,
  podModulePrefix: config.podModulePrefix,
  Resolver,

  ready() {
    $(config.APP.appLoadingSelector).toggleClass('hide');
    $(config.APP.rootElement).toggleClass(config.APP.bodyLoadingClass);
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

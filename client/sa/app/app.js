import Ember from 'ember';
import Resolver from 'ember-resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

let App;

Ember.MODEL_FACTORY_INJECTIONS = true;

App = Ember.Application.extend({
  modulePrefix: config.modulePrefix,
  podModulePrefix: config.podModulePrefix,
  Resolver,

  /**
   * Callback after app has finished initializing. Responsible for hiding the app's 'loading' animation DOM.
   * Assumes config.APP.appLoadingSelector will specify how to find the 'loading' DOM. Uses that selector to
   * obtain a handle to the DOM, then remove it from the HTML document.
   * @public
   */
  ready() {
    Ember.$(config.APP.appLoadingSelector).remove();
    Ember.$(config.APP.rootElement).removeClass(config.APP.bodyLoadingClass);
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

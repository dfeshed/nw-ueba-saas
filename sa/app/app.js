import Ember from 'ember';
import Resolver from './resolver';
import loadInitializers from 'ember-load-initializers';
import config from './config/environment';

const {
  Application,
  $
} = Ember;

Ember.MODEL_FACTORY_INJECTIONS = true;

const App = Application.extend({
  modulePrefix: config.modulePrefix,
  podModulePrefix: config.podModulePrefix,
  Resolver,

  engines: {
    investigate: {
      dependencies: {
        services: [
          '-document',
          'dateFormat',
          'timeFormat',
          'timezone',
          'i18n'
        ]
      }
    }
  },

  /**
   * Callback after app has finished initializing. Responsible for hiding the app's 'loading' animation DOM.
   * Assumes config.APP.appLoadingSelector will specify how to find the 'loading' DOM. Uses that selector to
   * obtain a handle to the DOM, then remove it from the HTML document.
   * @public
   */
  ready() {
    $(config.APP.appLoadingSelector).remove();
    $(config.APP.rootElement).removeClass(config.APP.bodyLoadingClass);
  }
});

loadInitializers(App, config.modulePrefix);

export default App;

import Ember from 'ember';

const {
  getOwner,
  Route,
  $
} = Ember;

export default Route.extend({

  activate() {
    let config = getOwner(this).resolveRegistration('config:environment');
    $(config.APP.appLoadingSelector).addClass('hide');
    $(config.APP.rootElement).removeClass(config.APP.bodyLoadingClass);
  },

  actions: {
    loading() {
      let config = getOwner(this).resolveRegistration('config:environment');
      $(config.APP.appLoadingSelector).toggleClass('hide');
      $(config.APP.rootElement).toggleClass(config.APP.bodyLoadingClass);
    }
  }

});

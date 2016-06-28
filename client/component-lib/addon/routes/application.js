import Ember from 'ember';
const { getOwner } = Ember;

export default Ember.Route.extend({

  activate() {
    let config = getOwner(this).resolveRegistration('config:environment');
    Ember.$(config.APP.appLoadingSelector).addClass('hide');
    Ember.$(config.APP.rootElement).removeClass(config.APP.bodyLoadingClass);
  },

  actions: {
    loading() {
      let config = getOwner(this).resolveRegistration('config:environment');
      Ember.$(config.APP.appLoadingSelector).toggleClass('hide');
      Ember.$(config.APP.rootElement).toggleClass(config.APP.bodyLoadingClass);
    }
  }

});

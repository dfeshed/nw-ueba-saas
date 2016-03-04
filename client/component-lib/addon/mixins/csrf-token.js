import Ember from 'ember';
import getOwner from 'ember-getowner-polyfill';

/**
  Responsible for retrieving the parent application's csrfLocalstorageKey from it's local config.
  @returns {string} config.csrfLocalstorageKey
  @public
*/
export default Ember.Mixin.create({

  csrfLocalstorageKey: (function() {
    let config = getOwner(this).resolveRegistration('config:environment');
    return config.csrfLocalstorageKey;
  }).property()

});

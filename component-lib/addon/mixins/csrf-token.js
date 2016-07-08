import Ember from 'ember';
const { getOwner } = Ember;

/**
  Responsible for retrieving the parent application's csrfLocalstorageKey from it's local simple-auth config.
  @returns {string} config['ember-simple-auth'].csrfLocalstorageKey
  @public
*/
export default Ember.Mixin.create({

  csrfLocalstorageKey: (function() {
    let config = getOwner(this).resolveRegistration('config:environment');
    return config['ember-simple-auth'].csrfLocalstorageKey;
  }).property()

});

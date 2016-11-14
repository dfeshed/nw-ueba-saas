import Ember from 'ember';

const {
  getOwner,
  Mixin,
  computed
} = Ember;

/**
  Responsible for retrieving the parent application's csrfLocalstorageKey from it's local simple-auth config.
  @returns {string} config['ember-simple-auth'].csrfLocalstorageKey
  @public
*/
export default Mixin.create({

  csrfLocalstorageKey: computed(function() {
    const config = getOwner(this).resolveRegistration('config:environment');
    return config['ember-simple-auth'].csrfLocalstorageKey;
  })

});

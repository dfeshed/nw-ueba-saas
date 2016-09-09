import Ember from 'ember';

const {
  getOwner,
  Mixin,
  computed
} = Ember;

/**
 Responsible for retrieving the parent application's accessTokenKey from it's local simple-auth config.
 @returns {string} config['ember-simple-auth'].accessTokenKey
 @public
 */
export default Mixin.create({

  accessTokenKey: computed(function() {
    let config = getOwner(this).resolveRegistration('config:environment');
    return config['ember-simple-auth'].accessTokenKey;
  }),

  refreshTokenKey: computed(function() {
    let config = getOwner(this).resolveRegistration('config:environment');
    return config['ember-simple-auth'].refreshTokenKey;
  })
});

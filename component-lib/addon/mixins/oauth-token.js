import { getOwner } from '@ember/application';
import Mixin from '@ember/object/mixin';
import { computed } from '@ember/object';

/**
 Responsible for retrieving the parent application's accessTokenKey from it's local simple-auth config.
 @returns {string} config['ember-simple-auth'].accessTokenKey
 @public
 */
export default Mixin.create({

  accessTokenKey: computed(function() {
    const config = getOwner(this).resolveRegistration('config:environment');
    if (config && config['ember-simple-auth']) {
      return config['ember-simple-auth'].accessTokenKey;
    }
  }),

  refreshTokenKey: computed(function() {
    const config = getOwner(this).resolveRegistration('config:environment');
    if (config && config['ember-simple-auth']) {
      return config['ember-simple-auth'].refreshTokenKey;
    }
  })
});

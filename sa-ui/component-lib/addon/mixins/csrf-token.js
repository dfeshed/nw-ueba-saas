import { getOwner } from '@ember/application';
import Mixin from '@ember/object/mixin';
import { computed } from '@ember/object';

/**
  Responsible for retrieving the parent application's csrfLocalstorageKey from it's local simple-auth config.
  @returns {string} config['ember-simple-auth'].csrfLocalstorageKey
  @public
*/
export default Mixin.create({

  csrfLocalstorageKey: computed(function() {
    const config = getOwner(this).resolveRegistration('config:environment');

    // For cases (like engines) in development where simple-auth isn't configured
    // but we still want to log in, default the csrfLocalstorageKey in
    const authConfig = config['ember-simple-auth'] || { csrfLocalstorageKey: 'rsa-x-csrf-token' };
    return authConfig.csrfLocalstorageKey;
  })

});

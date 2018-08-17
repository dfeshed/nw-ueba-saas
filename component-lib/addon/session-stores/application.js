import { computed } from '@ember/object';
import LocalStorage from 'ember-simple-auth/session-stores/local-storage';

export default LocalStorage.extend({
  key: 'rsa-nw-ui-session',
  _isFastBoot: computed(function() {
    return false;
  })
});

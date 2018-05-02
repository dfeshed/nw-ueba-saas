import LocalStorage from 'ember-simple-auth/session-stores/local-storage';

export default LocalStorage.extend({
  key: 'rsa-nw-ui-session'
});
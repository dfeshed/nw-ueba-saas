import CookieStore from 'ember-simple-auth/session-stores/cookie';

export default CookieStore.extend({

  persist(data) {
    this.set('cookieExpirationTime', data.authenticated.expires_in);
    return this._super(...arguments);
  }

});

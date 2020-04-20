import Store from 'ember-simple-auth/session-stores/local-storage';
import RSVP from 'rsvp';

export default Store.extend({

  key: 'rsa-nw-ui-session',

  _isFastBoot: false,

  persist(data) {
    if (data.authenticated.access_token) {
      // To enable cross tab syncing, we need to keep the access_token and responseText properties in the session.
      // However, because the access_token already exists in an httpOnly cookie, it doesn't need to be a real value.
      // This stub allows us to preserve simple-auth functionality without exposing sensitive data.
      data.authenticated.access_token = 'xxx';
      data.authenticated.responseText = '';
    }

    this._lastData = data;
    data = JSON.stringify(data || {});
    localStorage.setItem(this.key, data);

    return RSVP.resolve();
  }

});

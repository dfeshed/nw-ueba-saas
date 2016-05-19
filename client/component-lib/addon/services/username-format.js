import Ember from 'ember';

export default Ember.Service.extend({

  localStorageKey: 'rsa::securityAnalytics::friendlyNamePreference',

  session: Ember.inject.service(),

  systemUsername: Ember.computed('session.session.content.authenticated.username', function() {
    return this.get('session.session.content.authenticated.username');
  }),

  friendlyUsername: null,

  username: Ember.computed('systemUsername', 'friendlyUsername', {
    get() {
      if (this.get('friendlyUsername')) {
        return this.get('friendlyUsername');
      } else {
        return this.get('systemUsername');
      }
    },

    set(key, value) {
      if (!value || value === '') {
        localStorage.removeItem(this.get('localStorageKey'));
        this.set('friendlyUsername', null);
        return this.get('systemUsername');
      } else {
        localStorage.setItem(this.get('localStorageKey'), value);
        this.set('friendlyUsername', value);
        return this.get('friendlyUsername');
      }
    }
  }),

  init() {
    let local = localStorage.getItem(this.get('localStorageKey'));

    if (local) {
      this.set('friendlyUsername', local);
    }

    this._super(arguments);
  }

});

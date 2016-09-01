import Ember from 'ember';

const {
  Service,
  computed,
  inject: {
    service
  }
} = Ember;

export default Service.extend({

  localStorageKey: 'rsa::securityAnalytics::friendlyNamePreference',

  session: service(),

  systemUsername: computed('session.session.content.authenticated.user.name', function() {
    return this.get('session.session.content.authenticated.user.name');
  }),

  friendlyUsername: null,

  username: computed('systemUsername', 'friendlyUsername', {
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

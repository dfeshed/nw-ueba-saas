import Ember from 'ember';

const {
  Service,
  computed
} = Ember;

export default Service.extend({

  localStorageKey: 'rsa::securityAnalytics::contextMenuPreference',

  defaultSelection: true,

  storeLocally(value) {
    localStorage.setItem(this.get('localStorageKey'), value);
  },

  init() {
    let localStorageSpacing = localStorage.getItem(this.get('localStorageKey'));
    let currentSelection = null;

    if ((localStorageSpacing === 'true') || (localStorageSpacing === true)) {
      currentSelection = true;
    } else if ((localStorageSpacing === 'false') || (localStorageSpacing === false)) {
      currentSelection = false;
    } else {
      currentSelection = this.get('defaultSelection');
    }

    this.set('enabled', currentSelection);
    this.storeLocally(currentSelection);
    this._super(arguments);
  },

  enabled: computed({
    get() {
      return this.get('_enabled');
    },

    set(key, value) {
      this.set('_enabled', value);
      this.storeLocally(value);
      return value;
    }
  })

});

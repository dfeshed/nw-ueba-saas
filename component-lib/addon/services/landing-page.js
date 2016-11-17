import Ember from 'ember';

const {
  Service,
  computed
} = Ember;

export default Service.extend({

  localStorageKey: 'rsa::securityAnalytics::landingPagePreference',

  options: [{
    key: 'protected.respond',
    label: 'userPreferences.defaultLandingPage.respond'
  }, {
    key: 'protected.monitor',
    label: 'userPreferences.defaultLandingPage.monitor'
  }, {
    key: 'protected.admin',
    label: 'userPreferences.defaultLandingPage.admin'
  }, {
    key: 'protected.investigate',
    label: 'userPreferences.defaultLandingPage.investigate'
  }],

  defaultSelection: 'protected.respond',

  init() {
    const localStorageSpacing = localStorage[this.get('localStorageKey')];
    const defaultSelection = this.get('defaultSelection');
    let currentSelection = null;

    if (localStorageSpacing) {
      currentSelection = localStorageSpacing;
    } else {
      currentSelection = defaultSelection;
    }

    this.set('selected', this.get('options').findBy('key', currentSelection));
    this.storeLocally(currentSelection);
    this._super(arguments);
  },

  storeLocally(value) {
    localStorage[this.get('localStorageKey')] = value;
  },

  selected: computed('selected', {
    get() {
      return this.get('_selected');
    },

    set(key, value) {
      this.set('_selected', value);
      this.storeLocally(value.key);
      return value;
    }
  })

});

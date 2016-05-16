import Ember from 'ember';

export default Ember.Service.extend({

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
    key: 'protected.explore',
    label: 'userPreferences.defaultLandingPage.explore'
  }],

  defaultSelection: 'protected.monitor',

  selected: null,

  init() {
    let localStorageSpacing = localStorage[this.get('localStorageKey')],
        defaultSelection = this.get('defaultSelection'),
        currentSelection = null;

    if (localStorageSpacing) {
      currentSelection = localStorageSpacing;
    } else {
      currentSelection = defaultSelection;
    }

    this.set('selected', this.get('options').findBy('key', currentSelection));
    this.storeLocally();
    this._super(arguments);
  },

  storeLocally() {
    localStorage[this.get('localStorageKey')] = this.get('selected.key');
  },

  selectionDidChange: Ember.observer('selected', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.storeLocally();
    });
  })

});

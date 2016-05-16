import Ember from 'ember';

export default Ember.Service.extend({

  localStorageKey: 'rsa::securityAnalytics::notificationsPreference',

  enabled: null,

  defaultSelection: true,

  storeLocally() {
    localStorage[this.get('localStorageKey')] = this.get('enabled');
  },

  init() {
    let localStorageSpacing = localStorage[this.get('localStorageKey')],
        currentSelection = null;

    if ((localStorageSpacing === 'true') || (localStorageSpacing === true)) {
      currentSelection = true;
    } else if ((localStorageSpacing === 'false') || (localStorageSpacing === false)) {
      currentSelection = false;
    } else {
      currentSelection = this.get('defaultSelection');
    }

    this.set('enabled', currentSelection);
    this.storeLocally();
    this._super(arguments);
  },

  enabledDidChange: Ember.observer('enabled', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.storeLocally();
    });
  })

});

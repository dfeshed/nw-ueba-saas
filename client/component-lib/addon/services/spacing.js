import Ember from 'ember';

export default Ember.Service.extend({

  localStorageKey: 'rsa::securityAnalytics::spacingPreference',

  options: [{
    key: 'tight',
    label: 'userPreferences.spacing.tight',
    styleClass: 'rsa-spacing-tight'
  }, {
    key: 'loose',
    label: 'userPreferences.spacing.loose',
    styleClass: 'rsa-spacing-loose'
  }],

  defaultSelection: 'loose',

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

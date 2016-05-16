import Ember from 'ember';

export default Ember.Service.extend({

  moment: Ember.inject.service(),

  localStorageKey: 'rsa::securityAnalytics::dateFormatPreference',

  options: [{
    key: 'MM/DD/YYYY',
    label: 'userPreferences.dateFormat.monthFirst'
  }, {
    key: 'DD/MM/YYYY',
    label: 'userPreferences.dateFormat.dayFirst'
  }, {
    key: 'YYYY/MM/DD',
    label: 'userPreferences.dateFormat.yearFirst'
  }],

  defaultSelection: 'MM/DD/YYYY',

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

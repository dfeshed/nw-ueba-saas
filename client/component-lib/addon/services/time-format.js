import Ember from 'ember';

export default Ember.Service.extend({

  localStorageKey: 'rsa::securityAnalytics::timeFormatPreference',

  options: [{
    key: '12hr',
    label: 'userPreferences.timeFormat.twelveHour',
    format: 'hh:mm'
  }, {
    key: '24hr',
    label: 'userPreferences.timeFormat.twentyFourHour',
    format: 'HH:mm'
  }],

  defaultSelection: '24hr',

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

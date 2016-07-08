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
    this.storeLocally(currentSelection);
    this._super(arguments);
  },

  storeLocally(value) {
    localStorage[this.get('localStorageKey')] = value;
  },

  selected: Ember.computed('selected', {
    get() {
      return this.get('_selected');
    },

    set(key, value) {
      this.set('_selected', value);
      this.storeLocally(value);
      return value;
    }
  })

});

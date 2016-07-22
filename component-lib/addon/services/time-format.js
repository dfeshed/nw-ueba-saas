import Ember from 'ember';

const {
  Service,
  computed
} = Ember;

export default Service.extend({

  localStorageKey: 'rsa::securityAnalytics::timeFormatPreference',

  options: [{
    key: '12hr',
    label: 'userPreferences.timeFormat.twelveHour',
    format: 'hh:mma'
  }, {
    key: '24hr',
    label: 'userPreferences.timeFormat.twentyFourHour',
    format: 'HH:mm'
  }],

  defaultSelection: '24hr',

  init() {
    let localStorageSpacing = localStorage[this.get('localStorageKey')];
    let defaultSelection = this.get('defaultSelection');
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
      this.storeLocally(value);
      return value;
    }
  })

});

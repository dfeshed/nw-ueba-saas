import Ember from 'ember';

const {
  Service,
  inject: {
    service
  },
  computed
} = Ember;

export default Service.extend({

  moment: service(),

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

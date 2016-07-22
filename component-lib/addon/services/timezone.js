import Ember from 'ember';
import moment from 'moment';

const {
  Service,
  computed
} = Ember;

export default Service.extend({

  localStorageKey: 'rsa::securityAnalytics::timeZonePreference',

  options: moment.tz.names(),

  defaultSelection: 'America/New_York',

  init() {
    let localStorageSpacing = localStorage[this.get('localStorageKey')];
    let defaultSelection = this.get('defaultSelection');
    let currentSelection = null;

    if (localStorageSpacing) {
      currentSelection = localStorageSpacing;
    } else {
      currentSelection = defaultSelection;
    }

    this.set('selected', currentSelection);
    this.storeLocally(currentSelection);
    this._super(arguments);
  },

  storeLocally(value) {
    localStorage.setItem(this.get('localStorageKey'), value);
  },

  selected: computed({
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

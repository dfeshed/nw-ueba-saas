import Ember from 'ember';
import moment from 'moment';

export default Ember.Service.extend({

  localStorageKey: 'rsa::securityAnalytics::timeZonePreference',

  options: moment.tz.names(),

  defaultSelection: 'America/New_York',

  init() {
    let localStorageSpacing = localStorage[this.get('localStorageKey')],
        defaultSelection = this.get('defaultSelection'),
        currentSelection = null;

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

  selected: Ember.computed({
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

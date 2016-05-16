import Ember from 'ember';
import moment from 'moment';

export default Ember.Service.extend({

  localStorageKey: 'rsa::securityAnalytics::timeZonePreference',

  options: moment.tz.names(),

  defaultSelection: 'America/New_York',

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

    this.set('selected', currentSelection);
    this.storeLocally();
    this._super(arguments);
  },

  storeLocally() {
    localStorage[this.get('localStorageKey')] = this.get('selected');
  },

  selectionDidChange: Ember.observer('selected', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.storeLocally();
    });
  })

});

import Ember from 'ember';
import computed from 'ember-computed-decorators';

const {
  Service,
  run
} = Ember;

export default Service.extend({

  localStorageKey: 'rsa::securityAnalytics::respondModePreference',

  options: ['card', 'list'],

  defaultSelection: 'card',

  localStorageEventOverviewKey: 'rsa::securityAnalytics::respondModePreference::eventOverview',

  // Default options that reflect view state
  defaultEventOverviewModeSelection: {
    alertId: null,
    showPanel: false,
    showFullPanel: true
  },

  @computed
  selectedEventOverviewOptions: {
    get() {
      let value;
      const localStorageEventOverviewModeSelection = localStorage[this.get('localStorageEventOverviewKey')];

      localStorageEventOverviewModeSelection ?
        value = JSON.parse(localStorageEventOverviewModeSelection) :
        value = this.get('defaultEventOverviewModeSelection');

      return value;
    },
    set(value) {
      const stringValue = JSON.stringify(value);
      const _this = this;
      run.once(function() {
        localStorage[_this.get('localStorageEventOverviewKey')] = stringValue;
      });
      return value;
    }
  },

  init() {
    const localStorageRespondMode = localStorage[this.get('localStorageKey')];
    const defaultSelection = this.get('defaultSelection');
    let currentSelection = null;

    if (localStorageRespondMode) {
      currentSelection = localStorageRespondMode;
    } else {
      currentSelection = defaultSelection;
    }

    this.set('selected', currentSelection);
    this._super(arguments);
  },

  @computed
  selected: {
    get() {
      return localStorage[this.get('localStorageKey')];
    },
    set(value) {
      const _this = this;
      run.once(function() {
        localStorage[_this.get('localStorageKey')] = value;
      });
      return value;
    }
  }

});

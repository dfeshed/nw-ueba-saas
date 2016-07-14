import Ember from 'ember';

const {
  Service,
  computed,
  run
} = Ember;

export default Service.extend({

  localStorageKey: 'rsa::securityAnalytics::respondModePreference',

  options: ['card', 'list'],

  defaultSelection: 'card',

  init() {
    let localStorageRespondMode = localStorage[this.get('localStorageKey')],
      defaultSelection = this.get('defaultSelection'),
      currentSelection = null;

    if (localStorageRespondMode) {
      currentSelection = localStorageRespondMode;
    } else {
      currentSelection = defaultSelection;
    }

    this.set('selected', currentSelection);
    this._super(arguments);
  },

  selected: computed({
    get() {
      return localStorage[this.get('localStorageKey')];
    },
    set(key, value) {
      let _this = this;
      run.once(function() {
        localStorage[_this.get('localStorageKey')] = value;
      });
      return value;
    }
  })

});

import Ember from 'ember';

const {
  Service,
  getOwner,
  run,
  $,
  computed
} = Ember;

export default Service.extend({

  localStorageKey: 'rsa::securityAnalytics::themePreference',

  options: [{
    key: 'light',
    label: 'userPreferences.theme.light',
    styleClass: 'rsa-light'
  }, {
    key: 'dark',
    label: 'userPreferences.theme.dark',
    styleClass: 'rsa-dark'
  }],

  defaultSelection: 'dark',

  init() {
    run.next(this, function() {
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
    });
  },

  storeLocally(value) {
    run.next(this, function() {
      localStorage[this.get('localStorageKey')] = value;
    });
  },

  updateRootClass() {
    run.next(this, function() {
      let config = getOwner(this).resolveRegistration('config:environment'),
          rootEl = null,
          $root = null;

      if (config && config.APP && config.APP.rootElement) {
        rootEl = config.APP.rootElement;
      } else {
        rootEl = 'body';
      }

      $root = $(rootEl);

      if (this.get('options')) {
        this.get('options').forEach(function(option) {
          $root.removeClass(option.styleClass);
        });
      }

      if (this.get('selected.key')) {
        $root.addClass(`rsa-${this.get('selected.key')}`);
      } else {
        $root.addClass(`rsa-${this.get('selected')}`);
      }
    });
  },

  selected: computed('selected', {
    get() {
      return this.get('_selected');
    },

    set(key, value) {
      this.set('_selected', this.get('options').findBy('key', value));
      this.updateRootClass();
      this.storeLocally(value);
      return value;
    }
  })

});

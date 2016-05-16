import Ember from 'ember';
import getOwner from 'ember-getowner-polyfill';

export default Ember.Service.extend({

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

  selected: null,

  init() {
    Ember.run.next(this, function() {
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
    });
  },

  storeLocally() {
    Ember.run.next(this, function() {
      localStorage[this.get('localStorageKey')] = this.get('selected.key');
    });
  },

  updateRootClass() {
    Ember.run.next(this, function() {
      let config = getOwner(this).resolveRegistration('config:environment'),
          rootEl = null,
          $root = null;

      if (config && config.APP && config.APP.rootElement) {
        rootEl = config.APP.rootElement;
      } else {
        rootEl = 'body';
      }

      $root = Ember.$(rootEl);

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

  selectionDidChange: Ember.observer('selected', function() {
    Ember.run.once(this, function() {
      this.storeLocally();
      this.updateRootClass();
    });
  })

});

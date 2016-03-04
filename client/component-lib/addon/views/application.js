import Ember from 'ember';
import getOwner from 'ember-getowner-polyfill';

export default Ember.Component.extend({

  config: (function() {
    return getOwner(this).resolveRegistration('config:environment');
  }).property(),

  defaultTheme: (function() {
    return this.get('config').APP.defaultTheme;
  }).property('config'),

  defaultLocale: (function() {
    return this.get('config').i18n.defaultLocale;
  }).property('config'),

  /**
    Responsible for setting the parent application's default theme and locale from it's local config.
    @private
  */
  didInsertElement() {
    Ember.run.schedule('afterRender', this, function() {
      // if found, set the users locale from the local storage
      let localeKey = 'rsa-i18n-default-locale',
          locale = localStorage.getItem(localeKey) || this.get('defaultLocale');

      if (locale !== null) {
        this.set('i18n.locale', locale);
      }

      // set the users theme from the local storage, or a default
      let themeKey = 'rsa-theme-default-selected',
          theme = localStorage.getItem(themeKey) || this.get('defaultTheme');

      this.set('theme.selected', theme);
    });
  }
});

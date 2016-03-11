import Ember from 'ember';
import getOwner from 'ember-getowner-polyfill';

export default Ember.Component.extend({

  config: (function() {
    return getOwner(this).resolveRegistration('config:environment');
  }).property(),

  defaultTheme: (function() {
    return this.get('config').APP.themes.defaultTheme;
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
      this.handleLocale();
      this.handleTheme();
    });
  },

  handleLocale() {
    // if found, set the users locale from the local storage
    let selectedLocale = localStorage.getItem('rsa-i18n-default-locale'),
        defaultLocale = this.get('defaultLocale'),
        config = this.get('config'),
        locale = null;

    if (selectedLocale && config && (config.i18n.includedLocales || [ ]).contains(selectedLocale)) {
      locale = selectedLocale;
    } else {
      locale = defaultLocale;
    }

    this.set('i18n.locale', locale);
  },

  handleTheme() {
    // set the users theme from the local storage, or a default
    let selectedTheme = localStorage.getItem('rsa-theme-default-selected'),
        defaultTheme = this.get('defaultTheme'),
        config = this.get('config'),
        theme = null;

    if (selectedTheme && config && (config.APP.themes.includedThemes || [ ]).contains(selectedTheme)) {
      theme = selectedTheme;
    } else {
      theme = defaultTheme;
    }

    this.set('theme.selected', theme);
  }

});

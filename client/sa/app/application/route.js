/**
* @file Application route handler
* @public
*/

import Ember from 'ember';
import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';
import config from 'sa/config/environment';

export default Ember.Route.extend(ApplicationRouteMixin, {

  afterModel() {

    // if found, set the users locale from the local storage
    let localeKey = 'rsa-i18n-default-locale',
        locale = localStorage.getItem(localeKey);
    if (locale !== null) {
      this.set('i18n.locale', locale);
    }

    // set the users theme from the local storage, or a default
    let themeKey = 'rsa-theme-default-selected',
        theme = localStorage.getItem(themeKey) ||
                config.APP.defaultTheme ||
                'light';
    this.set('theme.selected', theme);
  },

  actions: {
    /**
    * Clears user session when users logs out
    * @listens onclick of logout
    * @public
    */
    invalidateSession() {
      this.get('session').invalidate();
    },

    /**
    * Changes the users locale.
    * Updates i18n service's locale key and sets the chosen locale in localStorage
    * @param locale {String} that holds selected locale
    * @public
    */
    changeLocale(locale) {
      this.set('i18n.locale', locale);
      localStorage.setItem('rsa-i18n-default-locale', locale);
    },

    /**
    * Changes the current theme selection. Assumes this.theme is the theme service.
    * @param theme
    * @public
    */
    changeTheme(theme) {
      this.set('theme.selected', theme);
      localStorage.setItem('rsa-theme-default-selected', theme);
    }
  }
});

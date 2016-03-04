import Ember from 'ember';
import layout from '../templates/components/rsa-action-select-locale';

export default Ember.Component.extend({

  layout,

  classNames: ['rsa-action-select-locale'],

  actions: {
    /**
    * Changes the users locale.
    * Updates i18n service's locale key and sets the chosen locale in localStorage
    * @param locale {String} that holds selected locale
    * @public
    */
    changeLocale(locale) {
      this.set('i18n.locale', locale);
      localStorage.setItem('rsa-i18n-default-locale', locale);
    }
  }

});

/**
* @file i18n controller
* @description controller responsible for handling i18n operations
*/

import Ember from "ember";

export default Ember.Controller.extend({
    i18n: Ember.inject.service(),

    actions: {
        /**
         * Changes the users locale.
         * updates i18n.locale key and sets the picked locale in localStorage
         * @param locale {String} that holds selected locale
         */
        changeLocale: function(locale) {
            var key = "rsa-i18n-default-locale";
            this.set("i18n.locale", locale);
            localStorage.setItem(key, locale);
        }
    }
});

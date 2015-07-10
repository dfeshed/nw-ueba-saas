/**
* @file Application route handler
* @author Srividhya Mahalingam
*/

import Ember from "ember";
import ApplicationRouteMixin from "simple-auth/mixins/application-route-mixin";

export default Ember.Route.extend(ApplicationRouteMixin,{

    afterModel: function() {
        // if found, set the users locale from the local storage
        var key = "rsa-i18n-default-locale",
            locale = localStorage.getItem(key);
        if (locale !== null) {
            this.set("i18n.locale", locale);
        }
    },

    actions:{
        /**
        * Clears user session when users logs out
        * @listens onclick of logout
        */
        invalidateSession: function() {
            this.get("session").invalidate();
        }
    }
});

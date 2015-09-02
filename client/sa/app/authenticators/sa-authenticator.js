/**
* @file Custom authenticator
* @description custom version of ember-cli-simple-auth that invokes our apis for
* login, logout and restore session
* @author Srividhya Mahalingam
*/

import Ember from "ember";
import ajax from "ic-ajax";
import Base from "simple-auth/authenticators/base";
import config from "sa/config/environment";

export default Base.extend({

    initialize: function() {
        // Force the client to always attempt to restore an existing session in case the
        // server-side session is still valid.
        if (config["simple-auth"].store === "simple-auth-session-store:localStorage") {
            var key = "ember_simple_auth:session";
            if (localStorage.getItem(key) === null) {
                var token = {
                    secure: {
                        authenticator: "authenticator:sa-authenticator"
                    }
                };
                localStorage.setItem(key, JSON.stringify(token));
            }
        }
    }.on("init"),

    getInfo: function() {
        return ajax("/api/info");
    },

    /**
     * Responsible for restoring the session after a page reload, given the persisted data from the session.
     * Should return a Promise that will resolve when the session is restored. The return value of that Promise
     * will be automatically put in localStorage, overwriting previously persisted session data.
     * See Simple Auth API docs: http://ember-simple-auth.com/ember-simple-auth-api-docs.html#SimpleAuth-Authenticators-Base-restore
     * @param data The persisted session data from the last valid login.
     * @returns {Ember.RSVP.Promise} A Promise that resolves with the session data to be persisted in local storage.
     */
    restore: function(data) {

        // We don't want to lose the persisted session data in localStorage, so we merge it on top of whatever other
        // info we need to fetch here.
        // @todo Not sure why we need to call getInfo here? Do we need that info persisted into localStorage?
        // If not, we could skip the getInfo call here and just return a Promise that resolves with the given "data".
        return this.getInfo().then(function(response) {
            return Ember.merge(response, data);
        });
    },
    /**
    * @function authenticate
    * @param credentials {Object} that holds username and password
    * @param credentials.identification {string} username of the user
    * @param credentials.password {string} password of the user
    */
    authenticate(credentials) {
        return ajax({
            type: "POST",
            url: "/api/user/login",
            data: credentials
        });
    },

    /**
    * @function invalidate
    */
    invalidate() {
        return ajax({
            type: "POST",
            url: "/api/user/logout"
        });
    }
});

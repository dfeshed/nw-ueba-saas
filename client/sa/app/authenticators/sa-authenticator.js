/**
* @file Custom authenticator
* @description custom version of ember-cli-simple-auth that invokes our apis for
* login, logout and restore session
* @author Srividhya Mahalingam
*/

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

    restore: function() {
        return this.getInfo();
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

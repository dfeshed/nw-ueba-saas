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
        return new Ember.RSVP.Promise(function(resolve, reject) {
            ajax("/api/info").then(function(data) {
                resolve(data);
            }, reject);
        });
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
        return new Ember.RSVP.Promise(function(resolve, reject) {
            Ember.$.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/api/user/login?username="+credentials.username+"&password="+credentials.password
            }).then(function(response) {
                Ember.run(function() {
                    resolve(response);
                });
            }, function(xhr) {
                Ember.run(function() {
                    reject(xhr.responseJSON || xhr.responseText);
                });
            });
        });
    },

    /**
    * @function invalidate
    */
    invalidate() {
        Ember.$.ajax({
            type: "POST",
            url: "/api/user/logout"
        });
        return Ember.RSVP.resolve();
    }
});

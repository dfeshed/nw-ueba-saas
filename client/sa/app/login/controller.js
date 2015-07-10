/**
* @file Login controller
* @description controller responsible for establishing new session
* @author Srividhya Mahalingam
*/

import config from '../config/environment';
import Ember from 'ember';

/**
 * Enumeration of authentication status.
 * @private
 * @type {String{}}
 */
var _STATUS = {
    INIT: "init",
    WAIT: "wait",
    ERROR: "error",
    SUCCESS: "success"
};

export default Ember.Controller.extend({

    /**
     * Username.
     * The user's inputted id from the login UI. Is set at run-time as user types.
     * @type String
     */
    username: "",

    /**
     * User password.
     * The user's inputted pwd from the login UI. Is set at run-time as user types.
     * @type String
     */
    password: "",

    /**
     * Reason why the last authentication attempt failed.  Is set dynamically at run-time by
     * the "authenticate" action. Is displayed in the login template. Is resets back to null after a successful attempt.
     * @type String
     * @default null
     */
    errorMessage: null,

    /**
     * Indicates the status of the login request. Either: "init", "wait", "err" or "success".
     * @type String
     * @default "init"
     */
    status: _STATUS.INIT,

    /**
     * Only false when the "uid" property is a non-empty string with some non-space character.
     * Used for enabling/disabling the login button in the UI.
     * @type Boolean
     */
    isLoginDisabled: function() {
        var uid = this.get("username");
        return (typeof uid !== "string") || !uid.trim().length || (this.get("status") === _STATUS.WAIT);
    }.property("username", "status"),

    /**
     * True unless status is "success".
     * Used for hiding the login UI once the user has successfully authenticated.
     * @type Boolean
     */
    isLoginVisible: function(){
        return this.get("status") !== _STATUS.SUCCESS;
    }.property("status"),

    actions: {
        /**
         * Establishes session when users logs in.
         * Updates the properties "status" and "errorMessage" accordingly, so that UI can
         * notify user of progress.
         * @listens login form submit action
         */
        authenticate: function() {

            // Update status to that UI can indicate that a login is in progress.
            this.set("status", _STATUS.WAIT);

            var me = this,
                credentials = this.getProperties("username", "password"),
                session = this.get("session"),
                i18nHelper = this.get("i18n");
            if (session) {

                // Calls the authenticate function specified in ENV['simple-auth']
                session.authenticate(config["simple-auth"].authenticate, credentials).then(

                    // Auth succeeded
                    function() {
                        me.set("errorMessage", null);
                        me.set("status", _STATUS.SUCCESS);
                    },

                    // Auth failed
                    function(message) {
                        me.set("errorMessage",
                            i18nHelper.t(
                                (message && message.status === 401) ?
                                    "login.unAuthorized" : "login.genericError"
                            ));
                        me.set("status", _STATUS.ERROR);
                        console.log("Authentication error:", message);
                    });
            }
        }
    }
});

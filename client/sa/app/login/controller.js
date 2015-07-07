/**
* @file Login controller
* @description controller responsible for establishing new session
* @author Srividhya Mahalingam
*/

import config from '../config/environment';
import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        /**
        * Establishes session when users logs in
        * @listens login form submit action
        */
        authenticate: function() {
            var me = this,
                credentials = this.getProperties('username', 'password'),
                session = this.get('session');
            if(session){
                // Calls the authenticate function specified in ENV['simple-auth']
                session.authenticate(config['simple-auth'].authenticate, credentials).then(null, function(message) {
                    if(message.status === 401){
                        me.set('errorMessage', me.t('login.unAuthorized'));
                    } else{
                        me.set('errorMessage', me.t('login.genericError'));
                    }
                }, function() {
                    me.set('errorMessage', me.t('login.genericError'));
                });
            }
        }
    }
});

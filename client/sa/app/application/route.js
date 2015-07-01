/**
* @file Application route handler
* @author Srividhya Mahalingam
*/

import Ember from 'ember';
import ApplicationRouteMixin from 'simple-auth/mixins/application-route-mixin';

export default Ember.Route.extend(ApplicationRouteMixin,{
    actions:{
        /**
        * Clears user session when users logs out
        * @listens onclick of logout
        */
        invalidateSession: function() {
            this.get('session').invalidate();
        }
    }
});

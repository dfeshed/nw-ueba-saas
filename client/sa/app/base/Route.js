/**
* @file Base route for SA
* @description Contains all the common logic that needs to be executed on all
* routes. ex.protected route access
* @author Srividhya Mahalingam
*/

import Ember from 'ember';
import AuthenticatedRouteMixin from 'simple-auth/mixins/authenticated-route-mixin';

/* Add AuthenticatedRouteMixin to ensure the routes extending from this
* route is not accessible for unauthenticated users
*/
export default Ember.Route.extend(AuthenticatedRouteMixin, {
});

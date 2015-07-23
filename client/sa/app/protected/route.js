/**
 * @file Protected route
 * Container for all sub-routes that require authentication.
 */
import Ember from "ember";
import AuthenticatedRouteMixin from "simple-auth/mixins/authenticated-route-mixin";

/**
 * Add AuthenticatedRouteMixin to ensure the routes extending from this
 * route are not accessible for unauthenticated users. Without authentication,
 * the routes will redirect to the "/login" route.
 */
export default Ember.Route.extend(AuthenticatedRouteMixin, {
});

import Ember from 'ember';
import UnauthenticatedRouteMixin from 'ember-simple-auth/mixins/unauthenticated-route-mixin';
/**
  Responsible for making the login route available to parent application.
  @public
*/
export default Ember.Route.extend(UnauthenticatedRouteMixin,{
});

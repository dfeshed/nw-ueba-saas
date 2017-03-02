import Route from 'ember-route';
import UnauthenticatedRouteMixin from 'ember-simple-auth/mixins/unauthenticated-route-mixin';

/**
  Responsible for making the login route available to parent application.
  @public
*/
export default Route.extend(UnauthenticatedRouteMixin, {
});

import Ember from 'ember';
import UnauthenticatedRouteMixin from 'ember-simple-auth/mixins/unauthenticated-route-mixin';

const { Route } = Ember;

/**
  Responsible for making the login route available to parent application.
  @public
*/
export default Route.extend(UnauthenticatedRouteMixin, {
});

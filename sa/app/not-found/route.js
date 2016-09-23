/**
 * @file not-found route
 * Defines the catch-all route that covers URL paths which do not match any other routes.  We could show a
 * Not Found error UI, but for now we simply redirect to the default route.
 * @public
 */
import Ember from 'ember';

const { Route } = Ember;

export default Route.extend({
});

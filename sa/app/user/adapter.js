/**
 * @file User Adapter
 * Adapter for the User model that extends from the application adapter
 * @public
 */

 /* remove this adapter once we change the user calls to go to core instead of
  response back-end */
import ApplicationAdapter from 'sa/application/adapter';

export default ApplicationAdapter.extend({
  namespace: 'response/api'
});

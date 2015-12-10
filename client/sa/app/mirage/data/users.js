/**
 * @description populates the records all user and profile related APIs.
 * @public
 */

export default function(server) {
  // Create 5 users with the schema listed in factories/users.js
  server.createList('users', 5);
}

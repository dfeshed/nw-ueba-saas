/**
 * @description populates the records for all all authentication related APIs.
 * @public
 */

export default function(server) {
  // Create the authenticated users listed in factories/login.js
  server.createList('info', 1);
}

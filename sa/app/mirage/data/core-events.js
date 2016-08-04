/**
 * @description populates the records all user and profile related APIs.
 * @public
 */

export default function(server) {
  // Create events with the schema listed in factories/core-events.js
  server.createList('core-events', 100);
}

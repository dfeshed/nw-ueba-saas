/**
 * Tears down mock socket servers and STOMP client connections.
 * Used in acceptance tests. After each test, we destroy the MockServer instances & STOMP clients we've created (if any),
 * so that the next test will not throw an error when it tries to re-create them.
 * @public
 */
export default function teardownSockets() {
  // disconnect all STOMP clients
  const request = this.application.__container__.lookup('service:request');
  request.disconnectAll();
}

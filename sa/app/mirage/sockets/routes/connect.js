/**
 * @file MockServer message handler that responds to CONNECT messages.
 * Registers a filter for a CONNECT request that responds with a CONNECTED frame so that STOMP client will treat the
 * connection as successful; otherwise, the STOMP client would never fire its success callback and subscriptions
 * would not be available.
 * @public
 */
export default function(server) {
  server.register({
    filter(message, frames) {
      try {
        return (frames[0].command === 'CONNECT');
      } catch (err) {
        return false;
      }
    },
    handler(message, frames, server) {
      server.sendFrame('CONNECTED');
    }
  });
}

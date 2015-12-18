/**
 * @file MockServer message handler that responds to CONNECT messages.
 * Responds with a CONNECTED frame so that STOMP client will treat the connection as successful; otherwise,
 * the STOMP client would never fire its success callback and subscriptions would not be available.
 * @public
 */

/**
 * Registers a message handler to the given mockserver instance for CONNECT messages.
 * The handler always returns a successful response.
 * @param {Object} server The MockServer instance, which is expected to have the following methods:
 * 'addMessageHandler' and 'sendFrame'.
 * @public
 */
export default function(server) {

  server.addMessageHandler(function(message, frames) {

    if (frames[0] && frames[0].command === 'CONNECT') {
      this.sendFrame('CONNECTED');
      return true;
    }
    return false;
  });

}

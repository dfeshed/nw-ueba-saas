/**
 * @file MockServer message handler that responds to echo requests.
 * Echo requests can be useful for development/testing scenarios when the client wants to dictate the message body
 * of the response that it expects.
 * @public
 */

/**
 * Adds a message handler that checks for MESSAGE frames with an 'echo' header. (The value of the header is ignored
 * as long as it is truthy.)  If found, responds with a MESSAGE frame that simply echoes the received message's body,
 * as well as its subscription id.
 * @param {Object} server The MockServer instance, which is expected to have the following methods:
 * 'addMessageHandler' and 'sendFrame'.
 * @public
 */
export default function(server) {

  server.addMessageHandler(function(message, frames) {
    let [firstFrame] = [frames[0]];
    if (firstFrame && firstFrame.headers.echo) {
      this.sendFrame(
          'MESSAGE',
          {
            'subscription': firstFrame.headers.id,
            'content-type': firstFrame.headers['content-type'] || '',
            'receipt-id': firstFrame.headers.receipt || ''
          },
          firstFrame.body,
          500
      );
      return true;
    }
    return false;
  });

}

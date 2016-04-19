/**
 * @file MockServer message handler that responds to DISCONNECT messages.
 * Register a filter for DISCONNECT requests that responds with a RECEIPT frame with a matching receipt id,
 * so that STOMP client will think disconnect was successful.
 * @public
 */
export default function(server) {
  server.register({
    filter(message, frames) {
      try {
        return (frames[0].command === 'DISCONNECT');
      } catch (err) {
        return false;
      }
    },
    handler(message, frames, server) {
      server.sendFrame('RECEIPT',
        {
          'receipt-id': frames[0].headers.receipt || ''
        });
    }
  });
}

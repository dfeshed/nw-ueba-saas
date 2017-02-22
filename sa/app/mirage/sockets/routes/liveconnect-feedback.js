export default function(server) {
  server.route('liveconnect-feedback', 'createRecord', function(message, frames, server) {

    const frame = (frames && frames[0]) || {};
    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: true,
      request: frame.body
    });
  });
}

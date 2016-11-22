export default function(server) {

  server.route('preferences', 'getPreference', function(message, frames, server) {
    const frame = (frames && frames[0]) || {};

    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: {
        contextMenuEnabled: true,
        notificationEnabled: true,
        defaultComponentUrl: 'protected.respond',
        timeZone: 'UTC',
        userLocale: 'en',
        dateFormat: 'MM/DD/YYYY',
        timeFormat: '24hr'
      },
      request: frame.body
    });
  });

  server.route('preferences', 'setPreference', function(message, frames, server) {
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

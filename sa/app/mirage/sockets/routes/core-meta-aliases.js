export default function(server) {
  // Mock the response for store.query('core-meta-alias') with the hash in the first record of 'core-meta-aliases' DB:

  server.route('core-meta-alias', 'query', function(message, frames, server) {
    const aliases = server.mirageServer.db['core-meta-aliases'];
    const [ aliasHash ] = aliases;

    let firstFrame = (frames && frames[0]) || {};
    server.sendFrame('MESSAGE', {
      subscription: (firstFrame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: aliasHash,
      request: firstFrame.body
    });
  });
}

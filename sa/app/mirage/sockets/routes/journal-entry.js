export default function(server) {

  server.route('journal-entry', 'createRecord', function(message, frames, server) {
    let frame = (frames && frames[0]) || {};
    server.mirageServer.db['journal-entry'].insert(frame.body);

    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: true,
      request: frame.body
    });
  });

  server.route('journal-entry', 'updateRecord', function(message, frames, server) {
    let frame = (frames && frames[0]) || {};
    let journal = server.mirageServer.db['journal-entry'].where({ journalId: frame.body.journalId, incidentId: frame.body.incidentId });
    if (journal.length > 0) {
      server.mirageServer.db['journal-entry'].update(journal[0].id, frame.body.journalMap);
    }

    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: true,
      request: frame.body
    });
  });

  server.route('journal-entry', 'deleteRecord', function(message, frames, server) {
    let frame = (frames && frames[0]) || {};
    server.mirageServer.db['journal-entry'].remove({ journalId: frame.body.journalId, incidentId: frame.body.incidentId });

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

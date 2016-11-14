export default function(server) {

  server.route('journal-entry', 'createRecord', function(message, frames, server) {
    const frame = (frames && frames[0]) || {};
    const incident = server.mirageServer.db.incidents.find(frame.body.incidentId);
    if (incident) {
      incident.notes = incident.notes || [];
      incident.notes.pushObject({
        id: frame.body.journalId,
        author: frame.body.journalMap.author,
        notes: frame.body.journalMap.notes,
        milestone: frame.body.journalMap.milestone,
        dateCreated: new Date()
      });
      server.mirageServer.db.incidents.update(frame.body.incidentId, incident);
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

  server.route('journal-entry', 'updateRecord', function(message, frames, server) {
    const frame = (frames && frames[0]) || {};
    const incident = server.mirageServer.db.incidents.find(frame.body.incidentId);
    if (incident) {
      incident.notes = incident.notes || [];
      const incidentNote = incident.notes.findBy('id', frame.body.journalId);
      if (incidentNote) {
        incidentNote.author = frame.body.journalMap.author;
        incidentNote.notes = frame.body.journalMap.notes;
        incidentNote.milestone = frame.body.journalMap.milestone;

        server.mirageServer.db.incidents.update(frame.body.incidentId, incident);
      }
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
    const frame = (frames && frames[0]) || {};
    const incident = server.mirageServer.db.incidents.find(frame.body.incidentId);
    if (incident) {
      incident.notes = incident.notes || [];
      const incidentNote = incident.notes.findBy('id', frame.body.journalId);
      if (incidentNote) {
        incident.notes.removeObject(incidentNote);
        server.mirageServer.db.incidents.update(frame.body.incidentId, incident);
      }
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
}

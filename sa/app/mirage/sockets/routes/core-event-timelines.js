export default function(server) {

  // Mock the response for a promise request of 'core-event-timeline' with the entire mirage DB collection
  // "core-event-timelines" (after randomizing the counts for some variety in testing):
  server.route('core-event-timeline', 'query', function(message, frames, server) {
    // let clone = [].concat(server.mirageServer.db['core-event-timelines']);
    // clone.forEach((datum) => {
    //   datum.count = parseInt(datum.count * Math.random(), 10);
    // });
    const hour = 3600000;
    // Add an hour to the current time so that the correct
    // time is calculated when we do "now -= hour" below.
    let now = new Date().getTime() + hour;
    let i = 24;
    let data = [];
    while (i-- >= 0) {
      data.unshift({
        name: 'hour',
        type: 'TimeT',
        value: now -= hour,
        count: Math.round(50000000 * Math.random())
      });
    }
    server.sendList(
      data,
      null,
      null,
      frames);
  });
}

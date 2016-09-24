export default function(server) {
  // Mock the response for a promise request of 'core-event-timeline'. This returns
  // a per-minute dataset for the previous 24 hours. Most data will be near zero
  // with several spikes to make it look realistic.
  server.route('core-event-timeline', 'query', function(message, frames, server) {
    const minute = 60000;
    const [frame] = frames;
    const timeRange = frame.body.filter.filterBy('field', 'timeRange');
    let end;
    if (timeRange) {
      end = timeRange[0].range.to * 1000 + minute;// add milliseconds
    } else {
      // Add a minute to the current time so that the correct time is calculated
      // when we do "now -= minute" below.
      end = new Date().getTime() + minute;// default to now
    }
    let i = 1440;
    let data = [];
    while (i-- >= 0) {
      data.unshift({
        name: 'minute',
        type: 'TimeT',
        value: end -= minute,
        count: Math.round(Math.tan(Math.random() * Math.PI / 2.01) * 1500)
      });
    }
    server.sendList(
      data,
      null,
      null,
      frames);
  });
}

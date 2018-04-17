// Data set for paged stream
// Keys are used as the 'markers'

const data = {
  abc: [1, 2, 3],
  def: [4, 5, 6],
  ghi: [7, 8, 9],
  jkl: [10, 11, 12],
  mno: [13, 14, 15],
  pqr: [16, 17, 18],
  stu: [19, 20, 21],
  vwx: [22, 23, 24],
  yz: [25, 26]
};

const markers = Object.keys(data);

export default {
  subscriptionDestination: '/test/subscription/paged-stream/_2',
  requestDestination: '/test/request/paged-stream/_2',
  delay: 250,
  message(frame) {
    let filters = [];
    if (frame.body) {
      const parsedBody = JSON.parse(frame.body);
      if (parsedBody.filters) {
        filters = parsedBody.filters;
      }
    }
    const markerFilter = filters.find((f) => f.field === 'marker');
    let [ nextMarker ] = markers;
    if (markerFilter) {
      const indexOfMarker = markers.indexOf(markerFilter.value);
      nextMarker = markers[indexOfMarker + 1];
    }

    const complete = nextMarker === 'yz';

    const meta = {
      complete
    };

    if (!complete) {
      meta.marker = nextMarker;
    }

    return {
      data: data[nextMarker],
      meta
    };
  }
};
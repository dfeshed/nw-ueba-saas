const minute = 60000;

export default {
  subscriptionDestination: '/user/queue/investigate/timeline',
  requestDestination: '/ws/investigate/timeline',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const timeRange = (bodyParsed.filter || []).filter((ele) => ele.field === 'timeRange');
    let end;
    if (timeRange.length > 0) {
      end = timeRange[0].range.to * 1000 + minute;  // add milliseconds
    } else {
      // Add a minute to the current time so that the correct time is calculated
      // when we do "now -= minute" below.
      end = new Date().getTime() + minute;// default to now
    }
    let i = 1440;
    const data = [];
    while (i-- >= 0) {
      data.unshift({
        name: 'minute',
        type: 'TimeT',
        value: end -= minute,
        count: Math.round(Math.tan(Math.random() * Math.PI / 2.01) * 1500)
      });
    }

    return {
      data
    };
  }
};



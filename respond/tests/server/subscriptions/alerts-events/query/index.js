import data from './data';

const len = data.length;

let counter = 0;

export default {
  subscriptionDestination: '/user/queue/alerts/events',
  requestDestination: '/ws/response/alerts/events',
  message(/* frame */) {
    // const end = Math.max(1, Math.round(Math.random() * len));
    const end = counter % len;
    counter++;
    return {
      data: data.slice(0, end + 1)
    };
  }
};
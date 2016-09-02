import allData from './data';

// for now, just going to grab 10 packets and call it a day
const data = allData.slice(0, 10);

export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/session-packets',
  requestDestination: '/ws/investigate/reconstruct/session-packets/stream',
  message(/* frame */) {
    return {
      meta: {
        complete: true,
        total: 10
      },
      data
    };
  }
};

import data from '../../core-event/stream/data';

export default {
  subscriptionDestination: '/user/queue/investigate/events/count',
  requestDestination: '/ws/investigate/events/count',
  message(/* frame */) {
    return {
      data: data().length
    };
  }
};
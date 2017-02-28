import data from './data';

export default {
  subscriptionDestination: '/user/queue/incident/details',
  requestDestination: '/ws/response/incident/details',
  message(/* frame */) {
    return {
      data
    };
  }
};
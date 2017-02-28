import data from './data';

export default {
  subscriptionDestination: '/user/queue/incident/storyline',
  requestDestination: '/ws/response/incident/storyline',
  message(/* frame */) {
    return {
      data
    };
  }
};
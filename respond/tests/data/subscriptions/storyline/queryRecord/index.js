import data from './data';

export default {
  subscriptionDestination: '/user/queue/incident/storyline',
  requestDestination: '/ws/respond/incident/storyline',
  message(/* frame */) {
    return {
      data
    };
  }
};
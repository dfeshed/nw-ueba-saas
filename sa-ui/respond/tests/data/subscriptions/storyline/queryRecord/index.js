import data from './data';

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/incident/storyline',
  requestDestination: '/ws/respond/incident/storyline',
  message(/* frame */) {
    return {
      data
    };
  }
};

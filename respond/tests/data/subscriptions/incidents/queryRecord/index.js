import data from './data';

export default {
  subscriptionDestination: '/user/queue/incident/details',
  requestDestination: '/ws/respond/incident/details',
  message(/* frame */) {
    return data;
  }
};
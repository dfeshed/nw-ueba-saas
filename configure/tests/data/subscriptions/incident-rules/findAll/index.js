import data from './data';

export default {
  subscriptionDestination: '/user/queue/alertrules',
  requestDestination: '/ws/respond/alertrules',
  message(/* frame */) {
    return {
      data
    };
  }
};
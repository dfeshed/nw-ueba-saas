import data from './data';

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/alertrules',
  requestDestination: '/ws/respond/alertrules',
  message(/* frame */) {
    return {
      data
    };
  }
};

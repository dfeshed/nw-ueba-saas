import data from './data';

export default {
  subscriptionDestination: '/user/queue/alertrules/rule',
  requestDestination: '/ws/respond/alertrules/rule',
  message(/* frame */) {
    return {
      data
    };
  }
};
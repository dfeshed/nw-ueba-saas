import data from './data';

export default {
  subscriptionDestination: '/user/queue/alertrules/fields',
  requestDestination: '/ws/respond/alertrules/fields',
  message(/* frame */) {
    return {
      data
    };
  }
};

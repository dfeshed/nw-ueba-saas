import data from './data';

export default {
  subscriptionDestination: '/user/queue/administration/context/metas',
  requestDestination: '/ws/administration/context/metas',
  message(/* frame */) {
    return {
      code: 0,
      data
    };
  }
};
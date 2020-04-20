import data from './data';

export default {
  subscriptionDestination: '/user/queue/contexthub/context/metas',
  requestDestination: '/ws/contexthub/context/metas',
  message(/* frame */) {
    return {
      code: 0,
      data
    };
  }
};
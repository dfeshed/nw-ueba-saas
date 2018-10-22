import data from './data';

export default {
  subscriptionDestination: '/user/queue/usm/group/rank/get-all',
  requestDestination: '/ws/usm/group/rank/get-all',
  message(/* frame */) {
    return {
      code: 0,
      data
    };
  }
};

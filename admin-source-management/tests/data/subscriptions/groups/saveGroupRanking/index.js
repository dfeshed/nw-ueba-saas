import data from './data';

export default {
  subscriptionDestination: '/user/queue/usm/group/rank/set',
  requestDestination: '/ws/usm/group/rank/set',
  message(/* frame */) {
    return {
      code: 0,
      data
    };
  }
};

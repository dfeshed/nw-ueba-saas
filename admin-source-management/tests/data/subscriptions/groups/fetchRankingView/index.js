import data from './data';

export default {
  subscriptionDestination: '/user/queue/usm/group/rank/effective-policy',
  requestDestination: '/ws/usm/group/rank/effective-policy',
  message(/* frame */) {
    return {
      code: 0,
      data
    };
  }
};


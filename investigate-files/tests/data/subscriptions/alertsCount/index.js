import data from './data';

export default {
  subscriptionDestination: '/user/queue/risk/score/file/context/get',
  requestDestination: '/ws/risk/score/file/context/get',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};

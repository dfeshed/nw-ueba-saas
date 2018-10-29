import data from './data';

export default {
  subscriptionDestination: '/user/queue/risk/score/host/context/get',
  requestDestination: '/ws/risk/score/host/context/get',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};

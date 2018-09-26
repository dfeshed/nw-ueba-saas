import data from './data';
export default {
  subscriptionDestination: '/user/queue/scoring/file/context/get',
  requestDestination: '/ws/scoring/file/context/get',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};

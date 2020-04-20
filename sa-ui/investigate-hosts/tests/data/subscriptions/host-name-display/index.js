import data from './data';

export default {
  subscriptionDestination: '/user/queue/endpoint/file/risky-hosts',
  requestDestination: '/ws/endpoint/file/risky-hosts',
  message() {
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
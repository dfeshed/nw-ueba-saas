import data from './data';

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/endpoint/certificate/search',
  requestDestination: '/ws/endpoint/certificate/search',
  message() {
    return {
      data
    };
  }
};

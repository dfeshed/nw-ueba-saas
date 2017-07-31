import data from './data';

export default {
  subscriptionDestination: '/cms/search/search',
  requestDestination: '/ws/cms/search/search',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};
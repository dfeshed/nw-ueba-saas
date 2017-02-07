const data = ['log', 'packet', 'log and packet'];

export default {
  subscriptionDestination: '/cms/search/get-resource-mediums',
  requestDestination: '/ws/cms/search/get-resource-mediums',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};



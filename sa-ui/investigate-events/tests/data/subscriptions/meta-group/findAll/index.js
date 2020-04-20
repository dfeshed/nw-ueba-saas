import metaGroups from './data';

export default {
  subscriptionDestination: '/user/queue/investigate/meta/groups/get-all',
  requestDestination: '/ws/investigate/meta/groups/get-all',
  message(/* frame */) {
    return {
      meta: {
        complete: false
      },
      data: metaGroups
    };
  }
};

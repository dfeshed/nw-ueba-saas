export default {
  subscriptionDestination: '/user/queue/contexthub/context/types',
  requestDestination: '/ws/contexthub/context/types',
  message(/* frame */) {
    return {
      data: [ 'IP', 'USER', 'HOST', 'DOMAIN' ]
    };
  }
};
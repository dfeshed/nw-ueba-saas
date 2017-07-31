export default {
  subscriptionDestination: '/user/queue/administration/context/types',
  requestDestination: '/ws/administration/context/types',
  message(/* frame */) {
    return {
      data: [ 'IP', 'USER', 'HOST', 'DOMAIN' ]
    };
  }
};
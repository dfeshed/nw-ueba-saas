const data = ['RECONNAISSANCE', 'DELIVERY', 'EXPLOITATION', 'INSTALLATION', 'COMMAND_AND_CONTROL', 'ACTION_ON_OBJECTIVE', 'CONTAINMENT', 'ERADICATION', 'CLOSURE'];

export default {
  subscriptionDestination: '/user/queue/options/investigation/milestone',
  requestDestination: '/ws/respond/options/investigation/milestone',
  message(/* frame */) {
    return {
      data,
      meta: {
        total: data.length
      }
    };
  }
};

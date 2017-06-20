export default {
  subscriptionDestination: '/user/queue/journal/create',
  requestDestination: '/ws/response/journal/create',
  message(/* frame */) {
    return {
      data: {
        id: '1',
        author: 'meiskm',
        notes: 'Hey, what is happening?',
        milestone: 'CONTAINMENT'
      }
    };
  }
};
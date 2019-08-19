export default {
  delay: 100,
  subscriptionDestination: '/user/queue/investigate/column/groups/delete-by-id',
  requestDestination: '/ws/investigate/column/groups/delete-by-id',
  message(frame) {
    const body = JSON.parse(frame.body);

    return {
      data: true,
      request: {
        id: body.id
      }
    };
  }
};

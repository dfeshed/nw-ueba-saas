export default {
  delay: 100,
  subscriptionDestination: '/user/queue/investigate/column/groups/set',
  requestDestination: '/ws/investigate/column/groups/set',
  message(frame) {
    const body = JSON.parse(frame.body);
    const num = Date.now();

    return {
      data: {
        'id': body.columnGroup.id ? body.columnGroup.id : `abc${num}`,
        'name': body.columnGroup.name,
        'ootb': false,
        'columns': body.columnGroup.fields
      }
    };
  }
};

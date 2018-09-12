export default {
  subscriptionDestination: '/user/queue/usm/group/set',
  requestDestination: '/ws/usm/group/set',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    return {
      ...bodyParsed
    };
  }
};

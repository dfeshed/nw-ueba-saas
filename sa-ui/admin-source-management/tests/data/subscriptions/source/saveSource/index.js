export default {
  subscriptionDestination: '/user/queue/usm/source/set',
  requestDestination: '/ws/usm/source/set',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    return {
      ...bodyParsed
    };
  }
};

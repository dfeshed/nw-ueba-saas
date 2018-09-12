export default {
  subscriptionDestination: '/user/queue/usm/policy/set',
  requestDestination: '/ws/usm/policy/set',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    return {
      ...bodyParsed
    };
  }
};

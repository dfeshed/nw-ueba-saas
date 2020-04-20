export default {
  subscriptionDestination: '/user/queue/usm/policy/saveandpublish',
  requestDestination: '/ws/usm/policy/saveandpublish',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    return {
      ...bodyParsed
    };
  }
};

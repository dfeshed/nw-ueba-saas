export default {
  subscriptionDestination: '/user/queue/usm/source/saveandpublish',
  requestDestination: '/ws/usm/source/saveandpublish',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    return {
      ...bodyParsed
    };
  }
};

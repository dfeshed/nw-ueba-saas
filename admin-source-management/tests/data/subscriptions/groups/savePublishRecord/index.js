export default {
  subscriptionDestination: '/user/queue/usm/group/saveandpublish',
  requestDestination: '/ws/usm/group/saveandpublish',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    return {
      ...bodyParsed
    };
  }
};

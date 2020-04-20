
export default {
  delay: 1,
  subscriptionDestination: '/user/queue/risk/score/settings/update',
  requestDestination: '/ws/respond/risk/score/settings/update',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const { data } = bodyParsed;
    return {
      code: 0,
      data
    };
  }
};

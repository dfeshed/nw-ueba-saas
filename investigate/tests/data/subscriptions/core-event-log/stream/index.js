import faker from 'faker';

export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/log-data',
  requestDestination: '/ws/investigate/reconstruct/log-data/stream',
  page(frame, sendMessage) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const { filter } = bodyParsed;

    // Find the list of session ids in the request's filter.
    const sessionIdFilter = (filter || []).find((ele) => ele.field === 'sessionIds');
    const sessionIds = sessionIdFilter && sessionIdFilter.values;

    // For each requested session id..
    (sessionIds || []).forEach((sessionId) => {

      // random delay
      setTimeout(() => {
        sendMessage({
          data: {
            sessionId,
            log: faker.lorem.words(1 + faker.random.number(50))
          }
        });
      }, faker.random.number(2000));
    });
  }
};

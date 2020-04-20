export default {
  subscriptionDestination: '/user/queue/investigate/validate/queries',
  requestDestination: '/ws/investigate/validate/queries',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const serverQueries = bodyParsed.data.queries;
    return {
      code: 0,
      data: serverResponse(serverQueries)
    };
  }
};

// To retrieve a server response, send out a query that contains xxx
// ex: action = 'xxx'
// If no errors, empty array is sent
const serverResponse = (queries) => {
  const map = {};
  queries.forEach((q) => {
    const decodedQ = decodeURIComponent(q);
    if (decodedQ.includes('xxx')) {
      map[q] = 'Server error';
    }
  });
  return map;
};
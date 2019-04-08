const CHARS = '0123456789qwertyuiopasdfghjklzxcvbnm';

const _generateId = () => {
  let id = '';
  for (let i = 0; i < 4; i++) {
    const idx = Math.floor(Math.random() * CHARS.length);
    id += CHARS[idx];
  }
  return id;
};

const _generateDataFromPredicates = (predicates) => {
  return predicates.map((predicate) => ({
    id: _generateId(),
    query: predicate,
    displayName: 'Cisco Logs',
    createdBy: 'Jay',
    createdOn: 1,
    lastUsed: 1
  }));
};

export default {
  subscriptionDestination: '/user/queue/investigate/predicate/get-by-query',
  requestDestination: '/ws/investigate/predicate/get-by-query',
  message(frame) {
    const { predicateRequests } = JSON.parse(frame.body);
    return {
      meta: {
        complete: false
      },
      data: _generateDataFromPredicates(predicateRequests)
    };
  }
};
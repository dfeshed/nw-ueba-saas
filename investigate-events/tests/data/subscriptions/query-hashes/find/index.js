const _generateDataFromHashes = (hashes) => {
  return hashes.map((hash, i) => ({
    id: `${i + 1}`,
    query: `action = 'value ${i + 1}'`,
    displayName: 'HTTP',
    createdBy: 'Jay',
    createdOn: 1,
    lastUsed: 1
  }));
};

export default {
  subscriptionDestination: '/user/queue/investigate/predicate/get-by-id',
  requestDestination: '/ws/investigate/predicate/get-by-id',
  message(frame) {
    const { predicateIds } = JSON.parse(frame.body);
    return {
      meta: {
        complete: false
      },
      data: _generateDataFromHashes(predicateIds)
    };
  }
};

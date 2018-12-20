export default {
  subscriptionDestination: '/user/queue/investigate/predicate/get-by-id',
  requestDestination: '/ws/investigate/predicate/get-by-id',
  message(frame) {
    if (frame.body.includes('predicateIds":["1f4","foo"]')) {
      return {
        meta: {
          complete: false
        },
        // TODO: flesh this out if frame has hashes, provide some
        // random pill data
        data: [
          {
            id: '1',
            query: 'action = \'foo\'',
            displayName: 'HTTP',
            createdBy: 'Jay',
            createdOn: 1,
            lastUsed: 1
          },
          {
            id: '2',
            query: 'action = \'bar\'',
            displayName: 'HTTP',
            createdBy: 'Jay',
            createdOn: 1,
            lastUsed: 1
          },
          {
            id: '3',
            query: 'action = \'baz\'',
            displayName: 'HTTP',
            createdBy: 'Jay',
            createdOn: 1,
            lastUsed: 1
          },
          {
            id: '4',
            query: 'action = \'bax\'',
            displayName: 'HTTP',
            createdBy: 'Jay',
            createdOn: 1,
            lastUsed: 1
          }
        ]
      };
    } else {
      return {
        meta: {
          complete: false
        },
        // TODO: flesh this out if frame has hashes, provide some
        // random pill data
        data: [
          {
            id: '1',
            query: 'action = \'foo\'',
            displayName: 'HTTP',
            createdBy: 'Jay',
            createdOn: 1,
            lastUsed: 1
          },
          {
            id: '2',
            query: 'action = \'bar\'',
            displayName: 'HTTP',
            createdBy: 'Jay',
            createdOn: 1,
            lastUsed: 1
          },
          {
            id: '3',
            query: 'action = \'baz\'',
            displayName: 'HTTP',
            createdBy: 'Jay',
            createdOn: 1,
            lastUsed: 1
          }
        ]
      };
    }
  }
};

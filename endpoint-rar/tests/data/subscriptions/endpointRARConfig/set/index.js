export default {
  subscriptionDestination: '/user/queue/endpoint/rar/set',
  requestDestination: '/ws/endpoint/rar/set',
  message(/* frame */) {
    return {
      data: {
        esh: 'esh-domain',
        servers: [
          {
            address: 'localhost',
            httpsPort: 443,
            httpsBeaconIntervalInSeconds: 900
          }
        ]
      },
      meta: {
        complete: true
      }
    };
  }
};

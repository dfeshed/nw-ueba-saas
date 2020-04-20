export default {
  subscriptionDestination: '/user/queue/endpoint/rar/test',
  requestDestination: '/ws/endpoint/rar/test',
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

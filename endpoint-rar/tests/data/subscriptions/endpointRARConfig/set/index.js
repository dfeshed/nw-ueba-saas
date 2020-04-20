export default {
  subscriptionDestination: '/user/queue/endpoint/rar/config/set',
  requestDestination: '/ws/endpoint/rar/config/set',
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

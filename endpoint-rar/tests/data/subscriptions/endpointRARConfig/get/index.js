export default {
  subscriptionDestination: '/user/queue/endpoint/rar/get',
  requestDestination: '/ws/endpoint/rar/get',
  message(/* frame */) {
    return {
      data: {
        enabled: true,
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

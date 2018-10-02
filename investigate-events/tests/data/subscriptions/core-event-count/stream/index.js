import data from '../../core-event/stream/data';

export default {
  subscriptionDestination: '/user/queue/investigate/events/count',
  requestDestination: '/ws/investigate/events/count',
  page(frame, sendMessage) {
    let meta;

    for (let i = 0; i <= 5; i++) {
      setTimeout((index) => {
        switch (index) {
          case 0:
            meta = {
              percent: 0,
              description: 'Queued'
            };
            break;

          // with warning
          // case 2:
          //   meta = {
          //     percent: 20,
          //     serviceId: '555d9a6fe4b0d37c827d402e',
          //     warning: 'A warning message'
          //   };
          //   break;

          case 5:
            // fatal error
            // meta = {
            //   percent: 50,
            //   serviceId: '555d9a6fe4b0d37c827d402e',
            //   error: 'Syntax Error'
            // };

            // everything is fine
            meta = {
              percent: 100,
              devices: [{
                serviceId: '555d9a6fe4b0d37c827d402d',
                on: true,
                elapsedTime: index * 3000,
                devices: [{
                  serviceId: '555d9a6fe4b0d37c827d4021',
                  on: true,
                  elapsedTime: index * 2000,
                  devices: [{
                    serviceId: '555d9a6fe4b0d37c827d402e', on: true, elapsedTime: index * 1000,
                    devices: [{
                      serviceId: '555d9a6fe4b0d37c827d402f', on: true, elapsedTime: index * 500
                    }]
                  }]
                }]
              }]
            };

            break;

          default:
            meta = {
              percent: index * 10,
              description: 'Executing'
            };
        }

        sendMessage({
          data: data().length,
          meta
        });
      }, i * 500, i);
    }
  }
};

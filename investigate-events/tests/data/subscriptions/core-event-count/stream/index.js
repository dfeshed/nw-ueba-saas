import data from '../../core-event/stream/data';

export default {
  subscriptionDestination: '/user/queue/investigate/events/count',
  requestDestination: '/ws/investigate/events/count',
  page(frame, sendMessage) {
    let meta;

    for (let i = 0; i <= 10; i++) {
      setTimeout((index) => {
        switch (index) {
          case 0:
            meta = {
              percent: 0,
              description: 'Queued'
            };
            break;

          case 10:
            meta = {
              percent: 100,
              devices: [{
                serviceId: '555d9a6fe4b0d37c827d402e',
                on: true,
                elapsedTime: index * 1000
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
          data: data.length,
          meta
        });
      }, i * 1000, i);
    }
  }
};

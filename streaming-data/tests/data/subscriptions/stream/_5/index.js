const outData = [
  [1, 2, 3, 4, 5],
  [6, 7, 8, 9, 10],
  [11, 12, 13, 14, 15],
  [16, 17, 18, 19, 20]
];

export default {
  subscriptionDestination: '/test/subscription/stream/_5',
  requestDestination: '/test/request/stream/_5',
  page(frame, sendMessage) {
    // send one at time = 0/1000/2000
    for (let i = 0; i < 4; i++) {
      setTimeout(function() {
        sendMessage({
          data: outData[i],
          meta: {
            complete: i === 3
          }
        });
      }, i * 500);
    }
  }
};


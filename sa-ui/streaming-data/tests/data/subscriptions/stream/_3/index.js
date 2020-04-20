const outData = [
  [1, 2, 3, 4, 5],
  [6, 7, 8, 9, 10],
  [11, 12, 13, 14, 15]
];

export default {
  subscriptionDestination: '/test/subscription/stream/_3',
  requestDestination: '/test/request/stream/_3',
  page(frame, sendMessage) {
    // send one at time = 0/1000/2000
    for (let i = 0; i < 3; i++) {
      setTimeout(function() {
        sendMessage({
          data: outData[i]
        });
      }, i * 1000);
    }
  }
};
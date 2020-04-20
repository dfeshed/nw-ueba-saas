const outData = [
  [1, 2, 3, 4, 5],
  [6, 7, 8, 9, 10]
];

export default {
  subscriptionDestination: '/test/subscription/stream/_13',
  requestDestination: '/test/request/stream/_13',
  page(frame, sendMessage) {
    // send one at time = 0 and time = 1000
    for (let i = 0; i < 5; i++) {
      setTimeout(function() {
        sendMessage({
          data: outData[i]
        });
      }, i * 1000);
    }
  }
};
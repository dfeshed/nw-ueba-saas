export default {
  subscriptionDestination: '/test/subscription/_3',
  requestDestination: '/test/request/_3',
  message(frame) {
    // takes input muliplier and returns array
    const returnArray = [];
    const mult = JSON.parse(frame.body).mult;

    for (let i = 0; i < 5; i++) {
      returnArray[i] = (i + 1) * mult;
    }

    return {
      data: returnArray
    };
  }
};
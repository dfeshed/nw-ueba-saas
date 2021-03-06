export default {
  subscriptionDestination: '/user/queue/investigate/eventanalysis/settings',
  requestDestination: '/ws/investigate/eventanalysis/settings',
  message() {
    function getRandomIntInclusive(min, max) {
      min = Math.ceil(min);
      max = Math.floor(max);
      // The maximum is inclusive and the minimum is inclusive
      return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    return {
      code: 0,
      data: { calculatedEventLimit: getRandomIntInclusive(100, 100000), legacyEventsEnabled: true }
    };
  }
};

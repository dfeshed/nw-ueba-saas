const data = [
  { 'id': '555d9a6fe4b0d37c827d402d', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR', 'version': '11.1.0.0' },
  { 'id': '555d9a6fe4b0d37c827d4021', 'displayName': 'loki-broker', 'name': 'BROKER', 'version': '11.1.0.0' },
  { 'id': '555d9a6fe4b0d37c827d402e', 'displayName': 'local-concentrator', 'name': 'CONCENTRATOR', 'version': '10.6.0.0' },
  { 'id': '555d9a6fe4b0d37c827d402f', 'displayName': 'qamac01-concentrator', 'name': 'CONCENTRATOR', 'version': '11.1.0.0' }
];

export default {
  subscriptionDestination: '/user/queue/investigate/endpoints',
  requestDestination: '/ws/investigate/endpoints',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};



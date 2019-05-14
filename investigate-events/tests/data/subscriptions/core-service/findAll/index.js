const data = [
  { 'id': '555d9a6fe4b0d37c827d402d', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR', 'version': '11.4.0.0', 'host': '10.4.61.33', 'port': 56005 },
  { 'id': '555d9a6fe4b0d37c827d4021', 'displayName': 'loki-broker', 'name': 'BROKER', 'version': '11.4.0.0', 'host': '10.4.61.28', 'port': 56003 },
  { 'id': '555d9a6fe4b0d37c827d402e', 'displayName': 'local-concentrator', 'name': 'CONCENTRATOR', 'version': '11.4.0.0', 'host': '127.0.0.1', 'port': 56005 },
  { 'id': '555d9a6fe4b0d37c827d402f', 'displayName': 'qamac01-concentrator', 'name': 'CONCENTRATOR', 'version': '11.4.0.0', 'host': '10.4.61.48', 'port': 56005 }
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

export default {
  subscriptionDestination: '/user/queue/investigate/summary',
  requestDestination: '/ws/investigate/summary',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};

const data = {
  'startMetaId': 1,
  'endMetaId': 12296047,
  'metaSize': 232845443,
  'metaMax': 142947249029,
  'startPacketId': 0,
  'endPacketId': 0,
  'packetSize': 0,
  'packetMax': 0,
  'startTime': 1506537600,
  'endTime': 1508178160,
  'startPacketTime': 0,
  'endPacketTime': 0,
  'startSessionId': 1,
  'endSessionId': 424349,
  'hostName': 'loki-device',
  'version': '11.1.0.0'
};
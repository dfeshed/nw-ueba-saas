export default {
  subscriptionDestination: '/user/queue/investigate/reconstruct/session-summary',
  requestDestination: '/ws/investigate/reconstruct/session-summary',
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
  'id': 14639,
  'packetFields': [
    {
      'length': 6,
      'name': 'eth.dst',
      'position': 0
    },
    {
      'length': 6,
      'name': 'eth.src',
      'position': 6
    },
    {
      'length': 2,
      'name': 'eth.type',
      'position': 12
    },
    {
      'length': 4,
      'name': 'ip.src',
      'position': 26
    },
    {
      'length': 4,
      'name': 'ip.dst',
      'position': 30
    },
    {
      'length': 1,
      'name': 'ip.proto',
      'position': 23
    },
    {
      'length': 2,
      'name': 'tcp.srcport',
      'position': 34
    },
    {
      'length': 2,
      'name': 'tcp.dstport',
      'position': 36
    }
  ],
  'summaryAttributes': [
    {
      'name': 'device',
      'value': 'devicename'
    },
    {
      'name': 'session',
      'value': '14639'
    },
    {
      'name': 'type',
      'value': 'network session'
    },
    {
      'name': 'source',
      'value': '192.168.58.6 : 65450'
    },
    {
      'name': 'destination',
      'value': '50.28.0.19 : 80'
    },
    {
      'name': 'service',
      'value': '80'
    },
    {
      'name': 'first packet time',
      'value': '2015-12-09 03:25:03.182'
    },
    {
      'name': 'last packet time',
      'value': '2015-12-09 03:25:07.414'
    },
    {
      'name': 'packet size',
      'value': '191,731 bytes'
    },
    {
      'name': 'payload size',
      'value': '176,135 bytes'
    },
    {
      'name': 'packet count',
      'value': '236'
    },
    {
      'name': 'flags',
      'value': 'Keep, Assembled, App Meta, Network Meta'
    }
  ]
};


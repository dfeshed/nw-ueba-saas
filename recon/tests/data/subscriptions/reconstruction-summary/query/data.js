const defaultPacketFields = [
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
];

const basicSummaryAttributes = [
  {
    'name': 'nwService',
    'key': '',
    'type': 'Text',
    'value': 'concentrator'
  },
  {
    'name': 'sessionId',
    'key': 'sessionid',
    'type': 'UInt64',
    'value': 59664
  },
  {
    'name': 'type',
    'key': '',
    'type': 'Text',
    'value': 'network'
  },
  {
    'name': 'source',
    'key': 'ip.src : port.src',
    'type': 'Text',
    'value': '10.4.61.43 : 7777'
  },
  {
    'name': 'destination',
    'key': 'ip.dst',
    'type': 'Text',
    'value': '10.4.61.53'
  },
  {
    'name': 'service',
    'key': 'service',
    'type': 'UInt32',
    'value': 0
  },
  {
    'name': 'firstPacketTime',
    'key': '',
    'type': 'TimeT',
    'value': 1491232720485
  },
  {
    'name': 'lastPacketTime',
    'key': '',
    'type': 'TimeT',
    'value': 1491232720513
  }
];

const summaryAttributesWithPayloads = [
  {
    'name': 'packetSize',
    'key': '',
    'type': 'UInt64',
    'value': 7361
  },
  {
    'name': 'requestPacketSize',
    'key': '',
    'type': 'UInt64',
    'value': 7360
  },
  {
    'name': 'responsePacketSize',
    'key': '',
    'type': 'UInt64',
    'value': 1
  },
  {
    'name': 'payloadSize',
    'key': '',
    'type': 'UInt64',
    'value': 5233
  },
  {
    'name': 'requestPayloadSize',
    'key': '',
    'type': 'UInt64',
    'value': 5233
  },
  {
    'name': 'responsePayloadSize',
    'key': '',
    'type': 'UInt64',
    'value': 0
  },
  {
    'name': 'packetCount',
    'key': '',
    'type': 'UInt64',
    'value': 2
  },
  {
    'name': 'requestPacketCount',
    'key': '',
    'type': 'UInt64',
    'value': 2
  }, {
    'name': 'responsePacketCount',
    'key': '',
    'type': 'UInt64',
    'value': 1
  }
];

const summaryAttributesNoPayloads = [
  {
    'name': 'packetSize',
    'key': '',
    'type': 'UInt64',
    'value': 3
  },
  {
    'name': 'requestPacketSize',
    'key': '',
    'type': 'UInt64',
    'value': 2
  },
  {
    'name': 'responsePacketSize',
    'key': '',
    'type': 'UInt64',
    'value': 1
  },
  {
    'name': 'payloadSize',
    'key': '',
    'type': 'UInt64',
    'value': 0
  },
  {
    'name': 'requestPayloadSize',
    'key': '',
    'type': 'UInt64',
    'value': 0
  },
  {
    'name': 'responsePayloadSize',
    'key': '',
    'type': 'UInt64',
    'value': 0
  },
  {
    'name': 'packetCount',
    'key': '',
    'type': 'UInt64',
    'value': 3
  },
  {
    'name': 'requestPacketCount',
    'key': '',
    'type': 'UInt64',
    'value': 2
  },
  {
    'name': 'responsePacketCount',
    'key': '',
    'type': 'UInt64',
    'value': 1
  }
];

const withPayloads = {
  'id': 14639,
  'packetFields': defaultPacketFields,
  'summaryAttributes': [...basicSummaryAttributes, ...summaryAttributesWithPayloads]
};

const noPayloads = {
  'id': 14639,
  'packetFields': defaultPacketFields,
  'summaryAttributes': [...basicSummaryAttributes, ...summaryAttributesNoPayloads]
};

export default {
  withPayloads,
  noPayloads
};

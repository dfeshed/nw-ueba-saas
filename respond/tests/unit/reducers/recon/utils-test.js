import { module, test } from 'qunit';
import { getAliases, getLanguage } from 'respond/reducers/respond/recon/utils';

module('Unit | Utils | recon');

const data1 = [
  {
    language: {
      format: 'IPv6',
      metaName: 'forward.ipv6',
      flags: 2147484691,
      displayName: 'Event Relay IPv6 Address'
    },
    aliases: {}
  },
  {
    language: {
      format: 'Text',
      metaName: 'lc.cid',
      flags: 2147484691,
      displayName: 'Collector ID'
    },
    aliases: {}
  },
  {
    language: {
      format: 'UInt8',
      metaName: 'medium',
      flags: 2147483987,
      displayName: 'Medium'
    },
    aliases: {
      1: 'Ethernet',
      2: 'Tokenring',
      3: 'FDDI',
      4: 'HDLC',
      5: 'NetWitness',
      6: '802.11',
      7: '802.11 Radio',
      8: '802.11 AVS',
      9: '802.11 PPI',
      10: '802.11 PRISM',
      11: '802.11 Management',
      12: '802.11 Control',
      13: 'DLT Raw',
      32: 'Logs',
      33: 'Correlation',
      34: 'Relationship'
    }
  },
  {
    language: {
      format: 'UInt16',
      metaName: 'tcp.srcport',
      flags: 2147484755,
      displayName: 'TCP Source Port'
    },
    aliases: {
      7: 'echo',
      9: 'discard',
      13: 'daytime',
      17: 'qotd',
      19: 'chargen',
      3389: 'rdp',
      5050: 'yahoo im',
      5060: 'sip',
      5190: 'aim',
      6346: 'gnuetella',
      6667: 'irc',
      9001: 'tor',
      9030: 'tor',
      9535: 'man'
    }
  },
  {
    language: {
      format: 'Text',
      metaName: 'msg.id',
      flags: 2147484179,
      displayName: 'Message ID'
    },
    aliases: {}
  },
  {
    language: {
      format: 'Text',
      metaName: 'nwe.callback_id',
      flags: 2147483922,
      displayName: 'NWE Callback Id'
    },
    aliases: {}
  },
  {
    language: {
      format: 'Text',
      metaName: 'parse.error',
      flags: 2147484435,
      displayName: 'Parse Error'
    },
    aliases: {}
  },
  {
    language: {
      format: 'Text',
      metaName: 'msg',
      flags: 2147483921,
      displayName: 'Message'
    },
    aliases: {}
  },
  {
    language: {
      format: 'UInt64',
      metaName: 'rid',
      flags: 2147483922,
      displayName: 'Remote Session ID'
    },
    aliases: {}
  },
  {
    language: {
      format: 'Text',
      metaName: 'sourcefile',
      flags: 2147484691,
      displayName: 'Source Filename'
    },
    aliases: {}
  },
  {
    language: {
      format: 'Text',
      metaName: 'process.vid.src',
      flags: 2147483923,
      displayName: 'Endpoint Source Process ID'
    },
    aliases: {}
  }
];

test('getAliases returns correctly for data', function(assert) {
  const aliases0 = getAliases(null);
  assert.propEqual(aliases0, {}, 'getAliases returns empty object if data is null');
  const aliases1 = getAliases(data1);
  assert.equal(typeof(aliases1), 'object', 'getAliases returns object');
  assert.ok(aliases1.hasOwnProperty('medium'), 'returned object has expected metaName');
  assert.ok(aliases1.hasOwnProperty('tcp.srcport'), 'returned object has expected metaName');
  assert.deepEqual(aliases1.medium, data1.find((item) => item.language.metaName === 'medium').aliases,
    'return object has correct value for metaName');
  assert.deepEqual(aliases1['tcp.srcport'], data1.find((item) => item.language.metaName === 'tcp.srcport').aliases,
    'return object has correct value for metaName');
});

test('getLanguage returns empty array if no data passed or empty data passed', function(assert) {
  const language1 = getLanguage([]);
  assert.equal(language1.length, 0, 'getLanguage returns empty array if empty data passed');
  const language2 = getLanguage(null);
  assert.equal(language2.length, 0, 'getLanguage returns empty array if data passed is null');
  const language3 = getLanguage(undefined);
  assert.equal(language3.length, 0, 'getLanguage returns empty array if data passed is undefined');
});

test('getLanguage returns correctly for data', function(assert) {
  const someLanguage = getLanguage(data1);
  assert.equal(someLanguage.length, data1.length, 'returned object is of correct length');
  someLanguage.forEach((item) => {
    assert.ok(item.hasOwnProperty('formattedName'), 'returned object has formattedName property');
    assert.ok(item, data1.find((originalItem) => originalItem === item), 'returned object has correct language elements');
  });

  const randomIndex = Math.floor(Math.random() * (data1.length));
  const anotherRandomIndex = Math.floor(Math.random() * (data1.length));
  assert.ok(data1.find((item) => item.language.metaName === someLanguage[randomIndex].metaName),
    `language array contains language array element at index ${randomIndex} from data`);
  assert.ok(data1.find((item) => item.language.metaName === someLanguage[anotherRandomIndex].metaName),
    `language array contains language array element at index ${anotherRandomIndex} from data`);
});

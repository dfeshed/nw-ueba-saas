import { faker }  from 'ember-cli-mirage';
import aliases from '../../helpers/meta-aliases';

const ENUMS_BY_META_NAME = {};

['medium', 'service', 'tcp.srcport', 'tcp.dstport', 'ip.proto'].forEach((metaName) => {
  ENUMS_BY_META_NAME[metaName] = Object.keys(aliases[metaName]);
});

function mockMetaValueForKey(metaName) {
  let enums = ENUMS_BY_META_NAME[metaName];
  if (enums) {
    return faker.random.arrayElement(enums);

  } else if (metaName.match(/ip\.src|ip\.dst/)) {
    return faker.internet.ip();

  } else if (metaName === 'agent') {
    return faker.internet.userAgent();

  } else if (metaName === 'tld') {
    return faker.internet.domainSuffix();

  } else if (metaName.match(/domain|host/)) {
    return faker.internet.domainName();

  } else if (metaName === 'username') {
    return faker.internet.userName();

  } else if (metaName === 'email') {
    return faker.internet.email();

  } else if (metaName === 'time') {
    return Number(new Date()) - parseInt(24 * 60 * 60 * 1000 * Math.random(), 10);
  } else {
    return faker.lorem.words(1)[0];
  }
}

function mockMetaValuesForKey(keyName, size) {
  let values = [];
  let i;
  for (i = 0; i < size; i++) {
    values.push(mockMetaValueForKey(keyName));
  }
  return values;
}

function addRandomCount(datum) {
  return {
    value: datum,
    count: Math.round(Math.random() * 10000) + 1
  };
}

function mockResultForKey(keyName, size) {
  return mockMetaValuesForKey(keyName, size).map(addRandomCount);
}

export default function(server) {

  server.route('core-meta-value', 'stream', function(message, frames, server) {
    const SIZE = 20;

    // What meta key are we fetching values for?
    let [ frame ] = frames;
    frame = frame || {};
    let { body } = frame;
    let { filter } = body;
    let metaFilter = (filter || []).findBy('field', 'metaName') || {};
    let metaName = metaFilter.value;

    let data = mockResultForKey(metaName, SIZE);

    // Simulate progress update response.
    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: [],
      request: frame.body,
      meta: {
        description: 'Please wait...',
        percent: '0'
      }
    },
    0);

    // Simulate 2nd progress update response.
    server.sendFrame('MESSAGE', {
      subscription: (frame.headers || {}).id || '',
      'content-type': 'application/json'
    }, {
      code: 0,
      data: [],
      request: frame.body,
      meta: {
        description: 'Almost ready...',
        percent: '50'
      }
    },
    1000);

    // Simulate completed response data.
    server.sendList(
      data.sort((a, b) => {
        return b.count - a.count;
      }),
      frames[0].body.page,
      null,
      frames,
      2000);
  });
}

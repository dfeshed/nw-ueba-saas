import faker from 'faker';
import { shared } from 'mock-server';

const aliases = shared.subscriptions.coreMetaAliasData;

const ENUMS_BY_META_NAME = {};
const SIZE = 20;

['medium', 'service', 'tcp.srcport', 'tcp.dstport', 'ip.proto'].forEach((metaName) => {
  ENUMS_BY_META_NAME[metaName] = Object.keys(aliases[metaName]);
});

function mockMetaValueForKey(metaName) {
  const enums = ENUMS_BY_META_NAME[metaName];
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
    return faker.lorem.words(1);
  }
}

function mockMetaValuesForKey(keyName, size) {
  const values = [];
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

export default {
  subscriptionDestination: '/user/queue/investigate/meta/values',
  requestDestination: '/ws/investigate/meta/values/stream',
  page(frame, sendMessage) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    if (bodyParsed.cancel) {
      return;
    }
    const metaFilter = (bodyParsed.filter || []).find((ele) => ele.field === 'metaName') || {};
    const metaName = metaFilter.value;
    const data = mockResultForKey(metaName, SIZE);

    // immediately send back "progress"
    sendMessage({
      data: [],
      meta: {
        description: 'Please wait...',
        percent: '0'
      }
    });

    // fake more progress a second later
    setTimeout(() => {
      sendMessage({
        data: [],
        meta: {
          description: 'Almost ready...',
          percent: '50'
        }
      });
    }, 1000);

    // and done
    setTimeout(() => {
      sendMessage({
        data: data.sort((a, b) => b.count - a.count),
        meta: {
          complete: true
        }
      });
    }, 2000);
  }
};

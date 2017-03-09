import faker from 'faker';
import { shared } from 'mock-server';

let eventList;
const NUMBER_OF_EVENTS = 500;
const aliases = shared.subscriptions.coreMetaAliasData;
const SERVICES = Object.keys(aliases.service).map(Number);
const TCP_SRC_PORTS = Object.keys(aliases['tcp.srcport']).map(Number);
const TCP_DST_PORTS = Object.keys(aliases['tcp.dstport']).map(Number);
const IP_PROTOS = Object.keys(aliases['ip.proto']);

const now = +(new Date());
const oneDayAgo = now - 24 * 60 * 60 * 1000;

const randInt = function(min, max) {
  return parseInt(min + (max - min) * Math.random(), 10);
};

const factory = function(i) {
  return {
    sessionId: i,
    time: oneDayAgo + i,
    metas: [
      [ 'service', faker.random.arrayElement(SERVICES) ],
      [ 'medium', faker.random.arrayElement([1, 32]) ],
      [ 'size', randInt(15, 2000) ],
      [ 'ip.proto', faker.random.arrayElement(IP_PROTOS) ],
      [ 'ip.src', faker.internet.ip() ],
      [ 'tcp.srcport', faker.random.arrayElement(TCP_SRC_PORTS) ],
      [ 'ip.dst', faker.internet.ip() ],
      [ 'tcp.dstport', faker.random.arrayElement(TCP_DST_PORTS) ]
    ]
  };
};

export default function() {
  if (eventList) {
    return eventList;
  }

  eventList = [];

  for (let i = 0; i < NUMBER_OF_EVENTS; i++) {
    eventList.push(factory(i));
  }

  return eventList;
}

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

const logAndNetworkMetas = [
  [ 'service', faker.random.arrayElement(SERVICES) ],
  [ 'size', randInt(15, 2000) ],
  [ 'ip.proto', faker.random.arrayElement(IP_PROTOS) ],
  [ 'ip.src', faker.internet.ip() ],
  [ 'tcp.srcport', faker.random.arrayElement(TCP_SRC_PORTS) ],
  [ 'ip.dst', faker.internet.ip() ],
  [ 'tcp.dstport', faker.random.arrayElement(TCP_DST_PORTS) ],
  [ 'medium', faker.random.arrayElement([1, 32]) ]
];

const endpointMetas = [
  [ 'ip.proto', faker.random.arrayElement(IP_PROTOS) ],
  [ 'medium', faker.random.number({ min: 2, max: 31 }) ],
  [ 'ip.src', faker.internet.ip() ],
  [ 'ip.dst', faker.internet.ip() ],
  [ 'param.dst', faker.internet.userAgent() ],
  [ 'nwe.callback_id', randInt(15, 2000) ]
];

const logAndNetworkFactory = function(i) {
  return {
    sessionId: i,
    time: oneDayAgo + i,
    metas: logAndNetworkMetas
  };
};

const endpointFactory = function(i) {
  return {
    sessionId: i,
    time: oneDayAgo + i,
    metas: endpointMetas
  };
};

export default function() {
  if (eventList) {
    return eventList;
  }

  eventList = [];

  for (let i = 1; i < NUMBER_OF_EVENTS; i++) {
    if (i % 3 == 0) {
      eventList.push(endpointFactory(i));
    } else {
      eventList.push(logAndNetworkFactory(i));
    }
  }

  return eventList;
}

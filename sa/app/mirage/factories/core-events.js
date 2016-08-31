/**
 * @description Specifies the list of keys and values that must be created and
 * populated for the /api/users call
 * @public
 */

import { faker, Factory }  from 'ember-cli-mirage';
import aliases from '../helpers/meta-aliases';

const MEDIUMS = Object.keys(aliases.medium);
const SERVICES = Object.keys(aliases.service);
const TCP_SRC_PORTS = Object.keys(aliases['tcp.srcport']);
const TCP_DST_PORTS = Object.keys(aliases['tcp.dstport']);
const IP_PROTOS = Object.keys(aliases['ip.proto']);

const now = +(new Date());
const oneDayAgo = now - 24 * 60 * 60 * 1000;

function randInt(min, max) {
  return parseInt(min + (max - min) * Math.random(), 10);
}
export default Factory.extend({
  sessionId: ((i) => {
    return i;
  }),
  time: ((i) => {
    return oneDayAgo + i;
  }),
  metas: (() => {
    return [
      [ 'service', faker.random.arrayElement(SERVICES) ],
      [ 'medium', faker.random.arrayElement(MEDIUMS) ],
      [ 'size', randInt(15, 2000) ],
      [ 'ip.proto', faker.random.arrayElement(IP_PROTOS) ],
      [ 'ip.src', faker.internet.ip() ],
      [ 'tcp.srcport', faker.random.arrayElement(TCP_SRC_PORTS) ],
      [ 'ip.dst', faker.internet.ip() ],
      [ 'tcp.dstport', faker.random.arrayElement(TCP_DST_PORTS) ]
    ];
  })
});

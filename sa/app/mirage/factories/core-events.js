/**
 * @description Specifies the list of keys and values that must be created and
 * populated for the /api/users call
 * @public
 */

import { faker, Factory }  from 'ember-cli-mirage';

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
      [ 'service', randInt(20, 80) ],
      [ 'medium', randInt(0, 3) ],
      [ 'size', randInt(15, 2000) ],
      [ 'ip.src', faker.internet.ip() ],
      [ 'tcp.srcport', randInt(80, 3000) ],
      [ 'ip.dst', faker.internet.ip() ],
      [ 'tcp.dstport', randInt(80, 3000) ]
    ];
  })
});

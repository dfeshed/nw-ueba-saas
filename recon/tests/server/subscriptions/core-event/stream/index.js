import faker from 'faker';

const randInt = function(min, max) {
  return faker.random.number({ min, max });
};

export default {
  subscriptionDestination: '/user/queue/investigate/events',
  requestDestination: '/ws/investigate/events/stream',
  message(/* frame */) {
    const fileName = faker.system.fileName();
    return {
      meta: {
        complete: true
      },
      data: [{
        sessionID: '',
        time: 1472585869119,
        metas: [
          [ 'service', randInt(20, 80) ],
          [ 'medium', randInt(0, 3) ],
          [ 'size', randInt(15, 2000) ],
          [ 'ip.src', faker.internet.ip() ],
          [ 'tcp.srcport', randInt(80, 3000) ],
          [ 'ip.dst', faker.internet.ip() ],
          [ 'tcp.dstport', randInt(80, 3000) ],
          [ 'payload', randInt(10000, 100000) ],
          [ 'eth.src', faker.internet.mac() ],
          [ 'eth.dst', faker.internet.mac() ],
          [ 'eth.type', 2048 ],
          [ 'ip.proto', 6 ],
          [ 'tcp.flags', 26 ],
          [ 'streams', randInt(1, 20) ],
          [ 'packets', randInt(10, 100) ],
          [ 'lifetime', randInt(10, 100) ],
          [ 'action', 'get' ],
          [ 'directory', faker.system.directoryPath() ],
          [ 'filename', fileName ],
          [ 'extension', fileName.split('.').pop() ]
        ]
      }]
    };
  }
};



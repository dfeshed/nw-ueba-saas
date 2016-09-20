import expect from 'expect.js';

import { startServer, testSingleMessage } from './util';

describe('mock server subscriptions', () => {

  let server;

  before((done) => {
    startServer((_server) => {
      server = _server;
      done();
    });
  });

  after(() => server.close());

  it('will respond when configured with message', (done) => {
    testSingleMessage('/test/subscription/_1', '/test/request/_1', [1,1,1,1,1], done);
  });

  it('will respond when configured with page that just returns once', (done) => {
    testSingleMessage('/test/subscription/_2', '/test/request/_2', [2,2,2,2,2], done);
  });

  it('will accept input for message and alter response accordingly', (done) => {
    testSingleMessage(
      '/test/subscription/_3', '/test/request/_3', [3,6,9,12,15], done, { mult: 3 });
  });

  it('will accept input for page and alter response accordingly', (done) => {
    testSingleMessage(
      '/test/subscription/_4', '/test/request/_4', [4,8,12,16,20], done, { mult: 4 });
  });

  it('will accept delay for response and delay that duration', (done) => {
    const start = new Date().getTime();
    testSingleMessage(
      '/test/subscription/_5', '/test/request/_5', [1,1,1,1,1], function() {
        const end = new Date().getTime();
        const diff = end - start;
        expect(diff).to.be.greaterThan(1000);
        done();
      });
  });

});


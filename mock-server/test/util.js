import path from 'path';
import SockJS from 'sockjs-client';
import expect from 'expect.js';

import server from '../';
const subscriptions = path.join(__dirname, 'harness', 'subscriptions');

const startServer = function(done) {
  server.startServer({
    subscriptionLocations: subscriptions
  }, done);
};

const testSingleMessage = function(
  subscriptionDestination,
  requestDestination,
  equalsValue,
  done,
  input = {}
) {

  const sock = new SockJS(`http://localhost:${process.env.MOCK_PORT}/socket`);

  sock.onopen = function() {
    // send generic connect message
    sock.send('CONNECT\nX-CSRF-TOKEN:null\nUpgrade:websocket\nAuthorization:Bearer null\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\u0000');
  };

  sock.onmessage = function(e) {
    // command always first line
    const command = e.data.split('\n')[0];

    // CONNECTED, just fire off SUBSCRIBE and SEND back to back
    if (command === 'CONNECTED') {
      sock.send(`SUBSCRIBE\nid:sub-0\ndestination:${subscriptionDestination}\n\n\u0000`);
      setTimeout(function() {
        sock.send(`SEND\nid:sub-0\ndestination:${requestDestination}\n\n${JSON.stringify(input)}\u0000`);
      }, 100);
    }

    // This is data response from subscription endpoint
    if (command === 'MESSAGE') {
      // body is always last line
      let body = e.data.split('\n').pop().trim();

      // bad character at end
      body = body.slice(0, -1);
      const responseObject = JSON.parse(body);
      expect(responseObject.data).to.eql(equalsValue);
      sock.close();
      done();
    }

  };
};

export {
  startServer,
  testSingleMessage
};
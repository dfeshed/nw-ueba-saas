import WebSocket from 'ws';

class MockServer {

  constructor(options = {}) {
    const {
      port = 32400
    } = options;

    this.port = port;
    this.pid = 1;
  }

  start() {
    const wss = new WebSocket.Server({
      host: 'localhost',
      port: this.port
    });

    wss.on('connection', (ws) => {
      ws.on('message', (message) => {
        this.handleMessage(ws, message);
      });
    });

    this.wss = wss;
  }

  handleMessage(ws, messageObj) {
    messageObj = JSON.parse(messageObj);
    const { message, params, route } = messageObj;
    if (message === 'addChan') {
      ws.send(JSON.stringify({
        flags: 1073938433,
        params: {
          tid: params.tid,
          pid: this.pid++,
          target: 1
        }
      }));
    } else if (route) {
      if (message === 'stream') {
        ws.send(JSON.stringify({
          route,
          flags: 1073938433,
          params: {
            streamresponse: 'One'
          }
        }));
        ws.send(JSON.stringify({
          route,
          flags: 1073938433,
          params: {
            streamresponse: 'Two'
          }
        }));
        ws.send(JSON.stringify({
          route,
          flags: 1073938433,
          params: {
            streamresponse: 'Three'
          }
        }));
      } else {
        ws.send(JSON.stringify({
          route,
          flags: 1073938433,
          params: {
            description: 'Foobar'
          }
        }));
      }
    }
  }

}

const mock = new MockServer();
mock.start();

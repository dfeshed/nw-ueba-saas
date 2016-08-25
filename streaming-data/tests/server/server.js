/* eslint-disable no-console */

import express from 'express';
import cors from 'cors';
import logger from 'morgan';
import bodyParser from 'body-parser';
import eWs from 'express-ws';

import { parseMessage, prepareConnectMessage } from './util';
import SUBSCRIPTIONS from './subscriptions';

const app = express();
eWs(app);

app.use(cors());
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

// generic info route used for all connections
// eslint-disable-next-line new-cap
const infoRoute = express.Router();
infoRoute.get('/', function(req, res) {
  res.json({ 'version': '10.6.0.0-SNAPSHOT','commit': 28,'changeset': 'f716b11','date': 1435711785000 });
});
app.use('/socket/info', infoRoute);

app.ws('/socket/*', function(ws /* , req */) {
  // send the sockjs open frame
  ws.send('o');

  // register message handler for this socket
  // this is per socket
  ws.on('message', function(msg) {
    const { frame, command } = parseMessage(msg);
    switch (command) {
      case 'CONNECT':
        ws.send(prepareConnectMessage());
        break;
      case 'SUBSCRIBE':
        if (SUBSCRIPTIONS[frame.headers.destination]) {
          ws.sendHandler = SUBSCRIPTIONS[frame.headers.destination];
        } else {
          console.error(`No handler exists for [[ ${frame.headers.destination} ]]`);
        }
        break;
      case 'SEND':
        if (ws.sendHandler) {
          const outMsg = ws.sendHandler.prepareSendMessage(frame);
          setTimeout(function() {
            if (!ws.clientDisconnected) {
              ws.send(outMsg);
            } else {
              console.info('Client disconnected, not sending message');
            }
          }, ws.sendHandler.delay || 1);
        }
        break;
      case 'DISCONNECT':
        ws.clientDisconnected = true;
        ws.terminate();
        break;
      default:
        console.warn('UNUSED COMMAND/FRAME', command, frame);
    }
  });
});

// error handlers
app.listen(9999, function() {
  console.info('We ready to go...');
});
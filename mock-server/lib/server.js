/* eslint-disable no-console */
/* global process */

import express from 'express';
import cors from 'cors';
import logger from 'morgan';
import bodyParser from 'body-parser';
import eWs from 'express-ws';

import { parseMessage, prepareConnectMessage, discoverSubscriptions } from './util';

const start = function(subscriptionLocations) {

  // dynamically build subscription configuration based on user location input
  const subscriptions = discoverSubscriptions(subscriptionLocations);

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
          if (subscriptions[frame.headers.destination]) {
            ws.sendHandler = subscriptions[frame.headers.destination];
          } else {
            console.error(`No handler exists for [[ ${frame.headers.destination} ]]`);
          }
          break;
        case 'SEND':
          if (ws.sendHandler) {
            const outMsg = ws.sendHandler.createSendMessage(frame);
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

          // DISCONNECT means the client has disconnected
          // so terminating should not be necessary
          // ws.terminate();
          break;
        default:
          console.warn('UNUSED COMMAND/FRAME', command, frame);
      }
    });
  });

  // error handlers
  app.listen(process.env.MOCK_PORT || 9999, function() {
    console.info('We ready to go...');
  });
};

export {
  start
};
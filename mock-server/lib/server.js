/* eslint-disable no-console */

import express from 'express';
import cors from 'cors';
import logger from 'morgan';
import bodyParser from 'body-parser';
import WebSocket from 'ws';
import eWs from 'express-ws';
import chalk from 'chalk';

import {
  parseMessage,
  createConnectMessage,
  createMessage,
  discoverSubscriptions,
  subscriptionList
} from './util';

const start = function({ subscriptionLocations }, cb) {

  // dynamically build subscription configuration based on user location input
  discoverSubscriptions(subscriptionLocations);

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
          ws.send(createConnectMessage());
          break;
        case 'SUBSCRIBE': {
          // get list of subscriptions and see if subscription being used is present
          const subscriptions = subscriptionList();
          if (subscriptions[frame.headers.destination]) {
            ws.subscriptionHandler = subscriptions[frame.headers.destination];
          } else {
            console.error(chalk.red(`No handler exists for [[ ${frame.headers.destination} ]]`));
          }
          break;
        }
        case 'SEND':
          _handleMessage(ws, frame);
          break;
        case 'DISCONNECT':
          // DISCONNECT means the client has disconnected
          // so terminating should not be necessary
          // ws.terminate();
          break;
        default:
          console.warn(chalk.yellow('UNUSED COMMAND/FRAME', command, frame));
      }
    });
  });

  // error handlers
  const server = app.listen(process.env.MOCK_PORT || 9999, function() {
    console.info(chalk.green('Mock server ready ready to go!'));
    if (cb) {
      cb(server);
    }
  });
};

const _handleMessage = function(ws, frame) {
  if (ws.subscriptionHandler) {

    // Create closure over ws state for possible
    // sending of sendMessage to `page` funtion
    const sendMessage = function(body = null) {
      if (!body && ws.page) {
        console.error(
          chalk.red(
            `If calling \`send\` function from \`page\`, must pass body object to callback, not processing this request any further: ${ws.subscriptionHandler.subscriptionDestination}`));
        return;
      }

      const outMsg = createMessage(ws.subscriptionHandler, frame, body);
      setTimeout(function() {
        if (isClosed(ws)) {
          console.info('Client disconnected, not sending message');
        } else {
          ws.send(outMsg);
        }
      }, ws.subscriptionHandler.delay || 1);
    };

    // single message back
    if (ws.subscriptionHandler.message) {
      sendMessage();
      return;
    }

    // allow subscription to paginate on its own
    if (ws.subscriptionHandler.page) {
      ws.subscriptionHandler.page(frame, sendMessage);
    }
  }
};

const isClosed = function(ws) {
  return ws.readyState === WebSocket.CLOSED || ws.readyState === WebSocket.CLOSING;
};

export {
  start
};
/* eslint-disable no-console */

import express from 'express';
import cors from 'cors';
import logger from 'morgan';
import bodyParser from 'body-parser';
import WebSocket from 'ws';
import eWs from 'express-ws';
import chalk from 'chalk';
import clone from 'clone';

import {
  parseMessage,
  createConnectMessage,
  createMessage,
  discoverSubscriptions,
  subscriptionList,
  mockAuthResponse,
  determineDelay
} from './util';

const start = function({ subscriptionLocations, routes }, cb) {

  // dynamically build subscription configuration based on user location input
  discoverSubscriptions(subscriptionLocations);

  const app = express();
  eWs(app);

  // SockJS v1.1.1 sets "withCredentials" XHR attribute to "true" for Cross Origin Resources (XHRCorsObject).
  // Therefore "/info" request fails with the following error.
  // A wildcard '*' cannot be used in the 'Access-Control-Allow-Origin' header when the credentials flag is true.

  // Configuring expected CORS response headers.
  // Access-Control-Allow-Origin=<Request Origin> to reflect the value of "Origin" Request header automatically.
  // Access-Control-Allow-Credentials=true.

  app.use(cors({ origin: true, credentials: true }));
  app.use(logger('dev'));
  app.use(bodyParser.json());
  app.use(bodyParser.urlencoded({ extended: false }));

  // generic info route used for all connections
  // eslint-disable-next-line new-cap
  const infoRoute = express.Router();
  infoRoute.get('/', function(req, res) {
    res.json({ 'version': '10.6.0.0-SNAPSHOT', 'commit': 28, 'changeset': 'f716b11', 'date': 1435711785000 });
  });
  app.use('/socket/info', infoRoute);

  // auth route which delivers a fake auth token response for login simulation
  // eslint-disable-next-line new-cap
  const authRoute = express.Router();
  authRoute.post('/', function(req, res) {
    const validUserNames = ['admin', 'local'];
    const validPasswords = ['netwitness', 'changeMe'];

    // any combo of the above common usernames/passwords will return successful authentication token
    if (validUserNames.includes(req.body.username) && validPasswords.includes(req.body.password)) {
      res.json(mockAuthResponse);
    } else {
      res.status(400);
      res.json({ 'error': 'invalid_grant', 'error_description': 'Bad credentials' });
    }
  });
  app.use('/oauth/token', authRoute);

  _processConfiguredRoutes(routes, app);

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
            ws.subscriptionHandler = null;  // clear out the last good subscription handler, if any
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
  const port = process.env.MOCK_PORT || 9999;
  const server = app.listen(port, function() {
    console.info(chalk.green(`Mock server ready ready to go on port ${port}!`));
    if (cb) {
      cb(server);
    }
  });
};

// App-wide cache of custom helpers. Helpers can be (optionally) defined by subscriptions at run-time. Helpers can
// then be leveraged by subscription handlers at any point in the future.  Useful for sharing tools/data across subscriptions.
const _subscriptionHelpers = {};

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

      const _handler = clone(ws.subscriptionHandler);
      const delay = determineDelay(ws.subscriptionHandler.delay);
      setTimeout(function() {
        if (_isClosed(ws)) {
          console.info('Client disconnected, not sending message');
        } else {
          const outMsg = createMessage(_handler, frame, body, _subscriptionHelpers);
          ws.send(outMsg);
          console.info(`Sent [[ ${_handler.requestDestination} ]] message after [[ ${delay} ms ]] delay`);
        }
      }, delay);
    };

    // single message back
    if (ws.subscriptionHandler.message) {
      sendMessage();
      return;
    }

    // allow subscription to paginate on its own
    if (ws.subscriptionHandler.page) {
      ws.subscriptionHandler.page(frame, sendMessage, _subscriptionHelpers);
    }
  }
};

const _isClosed = function(ws) {
  return ws.readyState === WebSocket.CLOSED || ws.readyState === WebSocket.CLOSING;
};

const _processConfiguredRoutes = function(routes, app) {
  if (routes) {
    routes.forEach((route) => {
      if (typeof(route.response) === 'function') {
        app.get(route.path, route.response);
      } else {
        app.get(route.path, (req, res) => res.json(route.response));
      }
    });
  }
};

export {
  start
};

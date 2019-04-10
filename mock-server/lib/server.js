/* eslint-disable no-console */

import path from 'path';
import express from 'express';
import cors from 'cors';
import logger from 'morgan';
import bodyParser from 'body-parser';
import WebSocket from 'ws';
import eWs from 'express-ws';
import chalk from 'chalk';
import clone from 'clone';
import cookieParser from 'cookie-parser';
import contextualActions from '../shared/contextual-actions';

import {
  parseMessage,
  createConnectMessage,
  createMessage,
  createSubscriptionReceiptMessage,
  discoverSubscriptions,
  subscriptionList,
  mockAuthResponse,
  determineDelay,
  dirListing
} from './util';

const start = function({ subscriptionLocations, routes }, cb, { urlPattern, customData } = {}) {

  // dynamically build subscription configuration based on user location input
  discoverSubscriptions(subscriptionLocations);

  const app = express();
  app.use(cookieParser());

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

  app.use('/locales/', express.static(path.join(__dirname, 'locales')), dirListing('locales'));

  app.use('/userpkistatus', function(req, res) {
    res.setHeader('Content-Type', 'text/html');
    res.send('off');
  });

  app.use('/display/security/securitybanner/get', function(req, res) {
    res.json({ 'data': [
      {
        'securityBannerEnabled': false,
        'securityBannerTitle': '',
        'securityBannerText': ''
      }
    ] });
  });

  // generic info route used for all connections
  // eslint-disable-next-line new-cap
  const infoRoute = express.Router();
  infoRoute.get('/', function(req, res) {
    res.json({ 'version': '10.6.0.0-SNAPSHOT', 'commit': 28, 'changeset': 'f716b11', 'date': 1435711785000 });
  });
  app.use('/socket/info', infoRoute);

  // action route for all context menu actions
  // eslint-disable-next-line new-cap
  const actionRoute = express.Router();
  actionRoute.get('/', function(req, res) {
    res.json(contextualActions);
  });
  app.use('/admin/contextmenu/configuration.json', actionRoute);

  // Rest endpoints for all custom server requests.
  if (urlPattern) {
    const datafun = require(customData);
    // eslint-disable-next-line new-cap
    const customRoute = express.Router();
    customRoute.get('/', function(req, res) {
      console.log(req.originalUrl);
      res.json(datafun.default(req.originalUrl));
    });
    customRoute.post('/', function(req, res) {
      console.log(req.originalUrl);
      res.json(datafun.default(req.originalUrl));
    });
    customRoute.patch('/', function(req, res) {
      console.log(req.originalUrl);
      res.json(datafun.default(req.originalUrl));
    });
    app.use(urlPattern, customRoute);
  }

  // auth route which delivers a fake auth token response for login simulation
  // eslint-disable-next-line new-cap
  const authRoute = express.Router();
  authRoute.post('/', function(req, res) {
    const validUserNames = ['admin', 'local'];
    const validPasswords = ['netwitness', 'changeMe'];

    // any combo of the above common usernames/passwords will return successful authentication token
    if (validUserNames.includes(req.body.username) && validPasswords.includes(req.body.password)) {
      res.cookie('access_token', mockAuthResponse.access_token, {
        httpOnly: true
      });
      res.json(mockAuthResponse);
    } else {
      res.status(400);
      res.json({ 'error': 'invalid_grant', 'error_description': 'Bad credentials' });
    }
  });
  app.use('/oauth/token', authRoute);

  app.use('/oauth/check', function(req, res) {
    res.cookie('access_token', mockAuthResponse.access_token, {
      httpOnly: true
    });

    const cookies = req.cookies;
    const authCookieString = cookies.access_token;

    if (!authCookieString) {
      // undefined authCookieString is expected during initial authentication
      res.send();
    } else {
      res.send(mockAuthResponse.access_token);
    }
  });

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
        case 'SUBSCRIBE':
          ws.send(createSubscriptionReceiptMessage(frame.headers));
          break;
        case 'SEND': {
          // get list of subscriptions and see if subscription being used is present
          const subscriptions = subscriptionList();
          if (subscriptions[frame.headers.destination]) {
            const subscriptionHandler = subscriptions[frame.headers.destination];
            _handleMessage(ws, frame, subscriptionHandler);
          } else {
            console.error(chalk.red(`No handler exists for [[ ${frame.headers.destination} ]]`));
          }
          break;
        }
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

const _handleMessage = function(ws, frame, subscriptionHandler) {
  if (subscriptionHandler) {

    // Create closure over ws state for possible
    // sending of sendMessage to `page` funtion
    const sendMessage = function(body = null, ignoreDelay = false) {
      if (!body && ws.page) {
        console.error(
          chalk.red(
            `If calling \`send\` function from \`page\`, must pass body object to callback, not processing this request any further: ${subscriptionHandler.subscriptionDestination}`));
        return;
      }

      const _handler = clone(subscriptionHandler);
      const delay = ignoreDelay ? 0 : determineDelay(subscriptionHandler.delay);
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
    if (subscriptionHandler.message) {
      sendMessage();
      return;
    }

    // allow subscription to paginate on its own
    if (subscriptionHandler.page) {
      subscriptionHandler.page(frame, sendMessage, _subscriptionHelpers);
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

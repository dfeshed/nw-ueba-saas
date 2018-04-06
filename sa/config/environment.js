/* eslint-env node */

const generateSocketConfiguration = require('./socketConfig');
const { addFeatureFlags } = require('../../common');
const mockPort = process.env.MOCK_PORT || 9999;
const mockServerUrl = `http://localhost:${mockPort}`;
const useMockServer = !process.env.NOMOCK;

module.exports = function(environment) {
  const ENV = {
    environment,
    useMockServer,
    mockServerUrl,
    mockPort,
    modulePrefix: 'sa',
    requestEula: true,
    rootURL: '/',
    locationType: 'auto',
    dateFormatDefault: 'MM/dd/yyyy',
    adminServerAvailable: true,
    timeFormatDefault: 'HR24',
    timezoneDefault: 'UTC',
    landingPageDefault: '/unified',
    investigatePageDefault: '/navigate',
    i18n: {
      defaultLocale: 'en',
      defaultFallback: true,
      includedLocales: ['en']
    },
    timezones: [{
      'displayLabel': 'UTC (GMT+00:00)',
      'offset': 'GMT+00:00',
      'zoneId': 'UTC'
    }],
    roles: [],
    flashMessageDefaults: {
      timeout: 5000,
      iconSize: 'larger',
      iconStyle: 'lined',
      type: 'info',
      types: ['info', 'success', 'warning', 'error']
    },
    moment: {
      includeLocales: ['en', 'ja'],
      includeTimezone: 'subset'
    },
    EmberENV: {
      FEATURES: {
        // Here you can enable experimental features on an ember canary build
        // e.g. 'with-controller': true
      },
      EXTEND_PROTOTYPES: {
        Function: true,
        String: true,
        Array: true,
        Date: false
      }
    },

    featureFlags: addFeatureFlags(environment),

    socketRoutes: generateSocketConfiguration(environment),
    socketDebug: false,
    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created

      // Optional artificial delay (in millisec) for testing the app's loading animation.
      // Used by the initializer "ready-delay". After animation has been sufficiently tested, either
      // delete the initializer, remove this line, or set value to zero.
      readyDelay: 1250, // 1500,
      rootElement: 'body'
    },
    'ember-simple-auth': {
      authenticate: 'authenticator:oauth-authenticator',
      authorizer: 'authorizer:oauth-authorizer',
      /* Local storage key that holds the CSRF token returned by the server */
      csrfLocalstorageKey: 'rsa-x-csrf-token',
      /* Local storage key that holds the OAuth access token returned by the Security Server */
      accessTokenKey: 'rsa-oauth2-jwt-access-token',
      /* Local storage key that holds the OAuth refresh token returned by the Security Server */
      refreshTokenKey: 'rsa-oauth2-jwt-refresh-token',
      routeAfterAuthentication: 'protected',
      routeIfAlreadyAuthenticated: 'protected'
    },
    'ember-load': {
      loadingIndicatorClass: 'rsa-application-loading'
    },
    contentSecurityPolicy: {

      // Allows us to use base64 encoded images in HTML/CSS without firing a CSP error.
      'img-src': "'self' data:",
      'connect-src': "'self' ws: wss:",
      'font-src': "'self' data:",
      'style-src': "'self' 'unsafe-inline'"
    },
    'ember-cli-d3-shape': {
      only: ['d3-array', 'd3-axis', 'd3-collection', 'd3-color', 'd3-dispatch', 'd3-ease', 'd3-format', 'd3-interpolate', 'd3-path', 'd3-selection', 'd3-shape', 'd3-scale', 'd3-time', 'd3-timer', 'd3-time-format', 'd3-transition']

    }
  };

  if (environment === 'development') {
    ENV.contentSecurityPolicy['connect-src'] = ["'self' ws: wss:", mockServerUrl];

    ENV.roles = [
      'viewAppliances',
      'searchLiveResources',
      'accessInvestigationModule',
      'respond-server.*',
      'integration-server.*'
    ];

    // ENV.APP.LOG_RESOLVER = true;
    // ENV.APP.LOG_ACTIVE_GENERATION = true;
    // ENV.APP.LOG_TRANSITIONS = true;
    // ENV.APP.LOG_TRANSITIONS_INTERNAL = true;
    // ENV.APP.LOG_VIEW_LOOKUPS = true;
    // Uncomment this line below if you want to debug socket calls in dev environment
    // ENV.socketDebug = true;
  }

  if (environment === 'test') {
    // this allows connections to be made to mock-sever running on
    ENV.contentSecurityPolicy['connect-src'] = ["'self' ws: wss:", mockServerUrl];

    ENV.roles = [
      'viewAppliances',
      'searchLiveResources',
      'accessInvestigationModule',
      'respond-server.*',
      'integration-server.*'
    ];

    // Testem prefers this...
    ENV.locationType = 'none';

    // keep test console output quieter
    ENV.APP.LOG_ACTIVE_GENERATION = false;
    ENV.APP.LOG_VIEW_LOOKUPS = false;

    // @workaround Disable readyDelay to avoid a synchronization issue with automated tests
    ENV.APP.readyDelay = 0;

    ENV.APP.rootElement = '#ember-testing';
    ENV.APP.autoboot = false;
  }

  if (environment === 'production') {
    // Ensure useMockServer is always false in production build
    ENV.useMockServer = false;
  }
  return ENV;
};

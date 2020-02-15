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
    modulePrefix: 'netwitness-ueba',
    requestEula: true,
    rootURL: '/',
    locationType: 'auto',
    dateFormatDefault: 'MM/dd/yyyy',
    adminServerAvailable: true,
    timeFormatDefault: 'HR24',
    timezoneDefault: 'UTC',
    landingPageDefault: '/investigate/entities',
    investigatePageDefault: '/investigate/entities',
    i18n: {
      defaultLocale: 'en-us',
      defaultFallback: true,
      includedLocales: ['en-us']
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
      includeLocales: true,
      includeTimezone: 'all'
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
      debounceDelay: 500,
      uebaTimeout: 100,
      rootElement: 'body'
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
    }
  };

  if (environment === 'production') {
    // Ensure useMockServer is always false in production build
    ENV.useMockServer = false;
  }
  return ENV;
};

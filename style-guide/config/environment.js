/* eslint-env node */
module.exports = function(environment) {
  const ENV = {
    modulePrefix: 'style-guide',
    environment,
    rootURL: '/',
    locationType: 'hash',
    dateFormatDefault: 'MM/dd/yyyy',
    timeFormatDefault: 'HR24',
    timezoneDefault: 'UTC',
    timezones: [{
      'displayLabel': 'UTC (GMT+00:00)',
      'offset': 'GMT+00:00',
      'zoneId': 'UTC'
    }],
    requestEula: true,
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
    i18n: {
      defaultLocale: 'en-us',
      defaultFallback: true,
      includedLocales: ['en-us']
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
    APP: {
      rootElement: 'body',
      // Optional artificial delay (in millisec) for testing the app's loading animation.
      // Used by the initializer "ready-delay". After animation has been sufficiently tested, either
      // delete the initializer, remove this line, or set value to zero.
      readyDelay: 1250 // 1500
    },
    'ember-simple-auth': {
      authenticate: 'authenticator:authenticator',
      authorizer: 'authorizer:authorizer',
      /* Local storage key that holds the CSRF token returned by the server */
      csrfLocalstorageKey: 'rsa-x-csrf-token'
    },
    'ember-load': {
      loadingIndicatorClass: 'rsa-application-loading'
    },
    contentSecurityPolicy: {
      // Allows us to use base64 encoded images in HTML/CSS without firing a CSP error.
      'img-src': "'self' data:",
      'connect-src': "'self' ws:",
      'font-src': "'self' data:",
      'style-src': "'self' 'unsafe-inline'"
    },
    'ember-cli-d3-shape': {
      only: ['d3-array', 'd3-axis', 'd3-collection', 'd3-color', 'd3-dispatch', 'd3-ease', 'd3-format', 'd3-interpolate', 'd3-path', 'd3-selection', 'd3-shape', 'd3-scale', 'd3-time', 'd3-timer', 'd3-time-format', 'd3-transition']
    }
  };

  if (environment === 'development') {
    // ENV.APP.LOG_RESOLVER = true;
    // ENV.APP.LOG_ACTIVE_GENERATION = true;
    // ENV.APP.LOG_TRANSITIONS = true;
    // ENV.APP.LOG_TRANSITIONS_INTERNAL = true;
    // ENV.APP.LOG_VIEW_LOOKUPS = true;

    ENV.visualTourRootUrl = '/visual-evolution';
  }

  if (environment === 'test') {
    // Testem prefers this...
    ENV.locationType = 'none';

    // keep test console output quieter
    ENV.APP.LOG_ACTIVE_GENERATION = false;
    ENV.APP.LOG_VIEW_LOOKUPS = false;

    // @workaround Disable readyDelay to avoid a synchronization issue with automated tests
    ENV.APP.readyDelay = 0;
    ENV.APP.rootElement = '#ember-testing';
  }

  if (environment === 'production') {
    ENV.rootURL = '/SA/SAStyle/production';
    ENV.visualTourRootUrl = 'https://libhq-ro.rsa.lab.emc.com/SA/SAStyle/e2e/tourShots';
  }

  return ENV;
};

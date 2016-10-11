/* eslint-disable */
module.exports = function(environment) {
  var ENV = {
    modulePrefix: 'style-guide',
    environment: environment,
    rootURL: '/',
    locationType: 'hash',
    moment: {
      includeLocales: ['en', 'ja'],
      includeTimezone: '2010-2020'
    },
    'i18n': {
      defaultLocale:'en',
      includedLocales: ['en', 'ja']
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
        Date: false,
      }
    },
    APP: {
      rootElement: 'body',
      // Optional artificial delay (in millisec) for testing the app's loading animation.
      // Used by the initializer "ready-delay". After animation has been sufficiently tested, either
      // delete the initializer, remove this line, or set value to zero.
      readyDelay: 1250, //1500,

      // Optional DOM selector for the app's "loading" animation that is displayed until app is ready.
      // Should match a DOM node in index.html.
      // Used by app's ready() handler to find & hide the loading animation.
      appLoadingSelector: '.rsa-application-loading .rsa-loader',
      bodyLoadingClass: 'rsa-application-loading'
    },
    'ember-simple-auth': {
        authenticate: 'authenticator:authenticator',
        authorizer: 'authorizer:authorizer',
        /* Local storage key that holds the CSRF token returned by the server */
        csrfLocalstorageKey: "rsa-x-csrf-token"
    },
    contentSecurityPolicy: {
      // Allows us to use base64 encoded images in HTML/CSS without firing a CSP error.
      "img-src": "'self' data:",
      'connect-src': "'self' ws:",
      'font-src': "'self' data:"
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
  }

  return ENV;
};

/* eslint-disable */

var generateSocketConfiguration = require('./socketConfig');

module.exports = function(environment) {
  var ENV = {
    modulePrefix: 'sa',
    environment: environment,
    baseURL: '/',
    locationType: 'auto',
    moment: {
      includeLocales: ['en', 'ja'],
      includeTimezone: '2010-2020'
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
    featureFlags: {
      'show-respond-route': true,
      'show-investigate-route': true
    },
    'ember-cli-mirage':  {},
    'ember-cli-mock-socket': {},
    socketRoutes: generateSocketConfiguration(environment),
    socketDebug: false,
    'i18n': {
      defaultLocale: 'en',
      includedLocales: ['en', 'ja']
    },
    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created

      // Optional artificial delay (in millisec) for testing the app's loading animation.
      // Used by the initializer "ready-delay". After animation has been sufficiently tested, either
      // delete the initializer, remove this line, or set value to zero.
      readyDelay: 1250, // 1500,

      // Optional DOM selector for the app's "loading" animation that is displayed until app is ready.
      // Should match a DOM node in index.html.
      // Used by app's ready() handler to find & hide the loading animation.
      appLoadingSelector: '.rsa-application-loading .rsa-loader',
      bodyLoadingClass: 'rsa-application-loading',
      rootElement: 'body'
    },
    'ember-simple-auth': {
      authenticate: 'authenticator:oauth-authenticator',
      authorizer: 'authorizer:oauth-authorizer',
      /* Local storage key that holds the CSRF token returned by the server */
      csrfLocalstorageKey: 'rsa-x-csrf-token',
      /* Local storage key that holds the OAuth token returned by the Security Server */
      accessTokenKey: 'rsa-oauth2-jwt-access-token'
    },
    contentSecurityPolicy: {

      // Allows us to use base64 encoded images in HTML/CSS without firing a CSP error.
      'img-src': "'self' data:",
      'connect-src': "'self' ws:",
      'font-src': "'self' data:",
      'style-src': "'self' 'unsafe-inline'"
    }
  };

  if (environment === 'development') {

    // ENV.APP.LOG_RESOLVER = true;
    // ENV.APP.LOG_ACTIVE_GENERATION = true;
    // ENV.APP.LOG_TRANSITIONS = true;
    // ENV.APP.LOG_TRANSITIONS_INTERNAL = true;
    // ENV.APP.LOG_VIEW_LOOKUPS = true;
    // Uncomment this line below if you want to debug socket calls in dev environment
    // ENV.socketDebug = true;
  }

  if (environment === 'test') {
    // Testem prefers this...
    ENV.baseURL = '/';
    ENV.locationType = 'none';

    // keep test console output quieter
    ENV.APP.LOG_ACTIVE_GENERATION = false;
    ENV.APP.LOG_VIEW_LOOKUPS = false;

    // @workaround Disable readyDelay to avoid a synchronization issue with automated tests
    ENV.APP.readyDelay = 0;

    ENV['ember-cli-mirage'].enabled = true;
    ENV['ember-cli-mock-socket'].enabled = true;
  }

  if (environment === 'production') {
    // Tweak feature flags for production here
    // ENV.featureFlags['show-respond-route'] = false;
  }

  return ENV;
};

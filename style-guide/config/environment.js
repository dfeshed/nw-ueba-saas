/* jshint node: true */

module.exports = function(environment) {
  var ENV = {
    modulePrefix: 'style-guide',
    environment: environment,
    baseURL: '/',
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
    ENV.baseURL = '/';
    ENV.locationType = 'none';

    // keep test console output quieter
    ENV.APP.LOG_ACTIVE_GENERATION = false;
    ENV.APP.LOG_VIEW_LOOKUPS = false;
    
    // @workaround Disable readyDelay to avoid a synchronization issue with automated tests
    ENV.APP.readyDelay = 0;
  }

  if (environment === 'production') {
    ENV.baseURL = '/SA/SAStyle/production';
  }

  return ENV;
};

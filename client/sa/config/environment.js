/* jshint node: true */

module.exports = function(environment) {
  var ENV = {
    modulePrefix: 'sa',
    environment: environment,
    baseURL: '/',
    locationType: 'auto',
    EmberENV: {
      FEATURES: {
        // Here you can enable experimental features on an ember canary build
        // e.g. 'with-controller': true
      }
    },
    'ember-cli-mirage':  {},
    'ember-cli-mock-socket': {},
    'socketURL': '/ws',
    'i18n': {
        defaultLocale:'en'
    },
    APP: {
        // Here you can pass flags/options to your application instance
        // when it is created

        // Optional artificial delay (in millisec) for testing the app's loading animation.
        // Used by the initializer "ready-delay". After animation has been sufficiently tested, either
        // delete the initializer, remove this line, or set value to zero.
        readyDelay: 0, //1500,

        // Optional DOM selector for the app's "loading" animation that is displayed until app is ready.
        // Should match a DOM node in index.html.
        // Used by app's ready() handler to find & hide the loading animation.
        appLoadingSelector: '#sa-app-spinner'
    },
    'simple-auth': {
        authenticate: 'authenticator:sa-authenticator'
    },
    contentSecurityPolicy: {

        // Allows us to use base64 encoded images in HTML/CSS without firing a CSP error.
        "img-src": "'self' data:"
    }
  };

  if (environment === 'development') {
    // ENV.APP.LOG_RESOLVER = true;
    // ENV.APP.LOG_ACTIVE_GENERATION = true;
    // ENV.APP.LOG_TRANSITIONS = true;
    // ENV.APP.LOG_TRANSITIONS_INTERNAL = true;
    // ENV.APP.LOG_VIEW_LOOKUPS = true;
    ENV['simple-auth'] = {
        authenticate: 'authenticator:sa-authenticator',
        store: 'simple-auth-session-store:local-storage'
    };
  }

  if (environment === 'test') {
    // Testem prefers this...
    ENV.baseURL = '/';
    ENV.locationType = 'none';

    // keep test console output quieter
    ENV.APP.LOG_ACTIVE_GENERATION = false;
    ENV.APP.LOG_VIEW_LOOKUPS = false;

    ENV.APP.rootElement = '#ember-testing';

    ENV['simple-auth'] = {
        authenticate: 'authenticator:sa-authenticator',
        store: 'simple-auth-session-store:ephemeral'
    };
    ENV['ember-cli-mirage'].enabled = true;
    ENV['ember-cli-mock-socket'].enabled = true;
  }

  if (environment === 'production') {

  }

  return ENV;
};

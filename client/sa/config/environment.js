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
    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created
      defaultLocale:'en'
    },
    'simple-auth': {
        authenticate: 'authenticator:sa-authenticator'
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
  }

  if (environment === 'production') {

  }

  return ENV;
};

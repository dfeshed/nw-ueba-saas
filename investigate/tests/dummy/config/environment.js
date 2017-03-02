/* eslint-env node */

const socketRouteGenerator = require('../../../config/socketRoutes');

module.exports = function(environment) {
  const ENV = {
    modulePrefix: 'dummy',
    environment,
    rootURL: '/',
    locationType: 'auto',
    EmberENV: {
      FEATURES: {
        // Here you can enable experimental features on an ember canary build
        // e.g. 'with-controller': true
      },
      EXTEND_PROTOTYPES: {
        // Prevent Ember Data from overriding Date.parse.
        Date: false
      }
    },

    featureFlags: {
      // some features that have been completed are turned off for 11.0
      // and will not be included until 11.1.
      //
      // for tests, we want to make sure we always have the features enabled
      // because we do not want to have to remove/rewrite tests to account
      // for defeatures and we do not want to deal with defeaturing inside
      // tests themselves. Messy messy.
      //
      // For dev we want to leave the features on to make dev more easy, as
      // those features are vital for using/navigating the application
      //
      // Set the trailing boolean to true if you want enable
      // the 11.1+ features in prod
      '11.1-enabled': environment !== 'production' ? true : false // < change last boolean to true/false to enable/disable in prod
    },

    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created
    },
    moment: {
      includeLocales: ['en', 'ja'],
      includeTimezone: '2010-2020'
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

    ENV.APP.rootElement = '#ember-testing';
  }

  // if (environment === 'production') {
  // }

  ENV.socketRoutes = socketRouteGenerator(environment);

  return ENV;
};

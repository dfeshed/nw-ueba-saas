/* eslint-env node */

const socketRouteGenerator = require('../../../config/socketRoutes');
const common = require('../../../../common');
const mockPort = process.env.MOCK_PORT || 9999;
const mockServerUrl = `http://localhost:${mockPort}`;
const useMockServer = !process.env.NOMOCK;

module.exports = function(environment) {
  const ENV = {
    modulePrefix: 'dummy',
    environment,
    mockServerUrl,
    useMockServer,
    mockPort,
    rootURL: '/',
    locationType: 'auto',
    dateFormatDefault: 'MM/dd/yyyy',
    timeFormatDefault: 'HR24',
    timezoneDefault: 'America/Los_Angeles',
    timezones: [
      {
        'displayLabel': 'UTC (GMT+00:00)',
        'offset': 'GMT+00:00',
        'zoneId': 'UTC'
      },
      {
        'displayLabel': 'America/Los Angeles (GMT-07:00)',
        'offset': 'GMT-07:00',
        'zoneId': 'America/Los_Angeles'
      }],
    flashMessageDefaults: {
      timeout: 5000,
      extendedTimeout: 0,
      iconSize: 'larger',
      iconStyle: 'lined',
      type: 'info',
      types: ['info', 'success', 'warning', 'error']
    },
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

    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created
    },
    moment: {
      includeLocales: ['en'],
      includeTimezone: 'subset'
    },
    i18n: {
      defaultLocale: 'en-us'
    },
    featureFlags: common.addFeatureFlags(environment)
  };

  if (environment === 'development') {
    // ENV.APP.LOG_RESOLVER = true;
    // ENV.APP.LOG_ACTIVE_GENERATION = true;
    // ENV.APP.LOG_TRANSITIONS = true;
    // ENV.APP.LOG_TRANSITIONS_INTERNAL = true;
    // ENV.APP.LOG_VIEW_LOOKUPS = true;
    ENV.roles = ['respond-server.*', 'integration-server.*'];
  }

  if (environment === 'test') {
    // Testem prefers this...
    ENV.locationType = 'none';

    // keep test console output quieter
    ENV.APP.LOG_ACTIVE_GENERATION = false;
    ENV.APP.LOG_VIEW_LOOKUPS = false;

    ENV.APP.rootElement = '#ember-testing';
    ENV.roles = ['respond-server.*', 'integration-server.*'];
  }

  // if (environment === 'production') {
  // }

  ENV.socketRoutes = socketRouteGenerator(environment);

  return ENV;
};

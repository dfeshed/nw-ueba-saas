/* jshint node: true */

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
      }
    },
    featureFlags: {
      'show-respond-route': true
    },
    'ember-cli-mirage':  {},
    'ember-cli-mock-socket': {},
    socketRoutes: {
      test: {       // Used for automated Ember tests. Remove this and tests will fail.
        socketUrl: '/test/socket',
        stream: {
          subscriptionDestination: '/user/queue/test/data',
          requestDestination: '/ws/test/data/stream'
        },
        query: {
          subscriptionDestination: '/user/queue/test/data',
          requestDestination: '/ws/test/data/query'
        },
        findRecord: {
          subscriptionDestination: '/user/queue/test/data',
          requestDestination: '/ws/test/data/find'
        },
        updateRecord: {
          subscriptionDestination: '/user/queue/test/data',
          requestDestination: '/ws/test/data/update'
        }
      },
      incident: {
        socketUrl: '/response',
        stream: {
          subscriptionDestination: '/topic/incidents/%@',
          requestDestination: '/threat/incidents',
          cancelDestination: '/ws/threats/cancel'
        },
        notify: {
            subscriptionDestination: '/topic/incidents/owner/%@',
            requestDestination: '/dummy/incidents/owner',
            cancelDestination: '/ws/threats/cancel'
        },
        findRecord: {
          'subscriptionDestination': '/user/queue/threats/incident',
          'requestDestination': '/ws/threats/incident/get'
        },
        updateRecord: {
          'subscriptionDestination': '/user/queue/threats/incidentsEdit',
          'requestDestination': '/ws/threats/incident/edit/'
        }
      }
    },
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
        appLoadingSelector: '.rsa-application-loading.rsa-icon',
        bodyLoadingClass: 'rsa-application-loading',
        rootElement: 'body',

        // Default theme to be applied when no theme preference is found in localeStorage.
        themes: {
          defaultTheme: 'dark',
          includedThemes: ['dark', 'light']
        }
      },
    'ember-simple-auth': {
        authenticate: 'authenticator:authenticator',
        authorizer: 'authorizer:authorizer',
        /* Local storage key that holds the CSRF token returned by the server */
        csrfLocalstorageKey: 'rsa-x-csrf-token'
      },
    contentSecurityPolicy: {

        // Allows us to use base64 encoded images in HTML/CSS without firing a CSP error.
        'img-src': "'self' data:",
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
    ENV.featureFlags['show-respond-route'] = false;
  }

  return ENV;
};

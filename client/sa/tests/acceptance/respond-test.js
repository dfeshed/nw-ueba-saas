import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import asyncFixtures from 'sa/mirage/scenarios/async-fixtures';
import config from 'sa/config/environment';

let oldFeatureFlags;

moduleForAcceptance('Acceptance | respond', {
  beforeEach() {
    oldFeatureFlags = config.featureFlags;
  },
  // After each test, destroy the MockServer instances we've created (if any), so that the next test will not
  // throw an error when it tries to re-create them.
  afterEach() {
    config.featureFlags = oldFeatureFlags;
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('disable respond feature flag, visiting /do/respond and check DOM ', function(assert) {
  config.featureFlags = {
    'show-respond-route': false
  };

  visit('/do/monitor');

  andThen(function() {
    assert.equal(find('.rsa-header-nav-respond').length, 0, '.rsa-header-nav-respond should not be in dom');
  });

});

test('enable respond feature flag, visiting /do/respond and check DOM ', function(assert) {
  config.featureFlags = {
    'show-respond-route': true
  };

  withFeature('show-respond-route');

  andThen(function() {
    return asyncFixtures(server, ['incident', 'alerts']);
  });

  visit('/do/respond');

  andThen(function() {
    assert.equal(currentPath(), 'protected.respond.index');
    assert.equal(find('.rsa-header-nav-respond').length, 1, '.rsa-header-nav-respond should be in dom');
  });

});

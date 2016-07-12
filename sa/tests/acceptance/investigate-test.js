import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import selectors from 'sa/tests/selectors';
import config from 'sa/config/environment';

const navLink = selectors.nav.investigateLink;
let oldFeatureFlags;

moduleForAcceptance('Acceptance | investigate', {
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

test('disable investigate route feature flag and confirm route is missing from app header nav', function(assert) {
  config.featureFlags = {
    'show-investigate-route': false
  };
  visit('/do/monitor');
  andThen(function() {
    assert.equal(find(navLink).length, 0, 'link to investigate route should not be in dom.');
  });

});

test('enable investigate route feature flag and confirm route is accessible', function(assert) {
  config.featureFlags = {
    'show-investigate-route': true
  };
  visit('/do/monitor');
  andThen(function() {
    assert.equal(find(navLink).length, 1, 'Link to Investigate route should be in DOM.');
    return click(navLink);
  });
  andThen(function() {
    assert.equal(currentPath(), 'protected.investigate.index', 'correct path was transitioned into.');
  });
});

test('investigate route redirects to index subroute if invalid subroute is requested', function(assert) {
  config.featureFlags = {
    'show-investigate-route': true
  };
  visit('/do/investigate/some-invalid-subroute');
  andThen(function() {
    assert.equal(currentPath(), 'protected.investigate.index', 'correct path was redirected into.');
  });
});


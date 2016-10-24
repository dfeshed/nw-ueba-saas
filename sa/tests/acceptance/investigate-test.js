import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import selectors from 'sa/tests/selectors';
import config from 'sa/config/environment';
import teardownSockets from 'sa/tests/helpers/teardown-sockets';

const navLink = selectors.nav.investigateLink;
let oldFeatureFlags;

moduleForAcceptance('Acceptance | investigate', {
  beforeEach() {
    oldFeatureFlags = config.featureFlags;
  },
  afterEach() {
    config.featureFlags = oldFeatureFlags;
    teardownSockets.apply(this, arguments);
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
  visit('/do/monitor');
});

test('investigate route redirects to index subroute if invalid subroute is requested', function(assert) {
  config.featureFlags = {
    'show-investigate-route': true
  };
  visit('/do/investigate/some-invalid-subroute');
  andThen(function() {
    assert.equal(currentPath(), 'protected.investigate.index', 'correct path was redirected into.');
  });
  visit('/do/monitor');
});


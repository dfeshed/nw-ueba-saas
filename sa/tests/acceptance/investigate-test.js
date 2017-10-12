import { skip } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import selectors from 'sa/tests/selectors';
import teardownSockets from 'sa/tests/helpers/teardown-sockets';

const navLink = selectors.nav.investigateLink;

moduleForAcceptance('Acceptance | investigate', {
  afterEach() {
    teardownSockets.apply(this, arguments);
  }
});

skip('enable investigate route feature flag and confirm route is accessible', function(assert) {
  visit('/monitor');
  andThen(function() {
    assert.equal(find(navLink).length, 1, 'Link to Investigate route should be in DOM.');
    return click(navLink);
  });
  andThen(function() {
    assert.equal(currentPath(), 'protected.investigate.index', 'correct path was transitioned into.');
  });
  visit('/monitor');
});

skip('investigate route redirects to index subroute if invalid subroute is requested', function(assert) {
  visit('/investigate/some-invalid-subroute');
  andThen(function() {
    assert.equal(currentPath(), 'protected.investigate.index', 'correct path was redirected into.');
  });
  visit('/monitor');
});


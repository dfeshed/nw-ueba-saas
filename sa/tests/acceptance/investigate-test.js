import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import teardownSockets from 'sa/tests/helpers/teardown-sockets';

moduleForAcceptance('Acceptance | investigate', {
  afterEach() {
    teardownSockets.apply(this, arguments);
  }
});

// Investigate default page is event query
test('investigate.index redirects to investigate.investigate-events.index', function(assert) {
  visit('/investigate/');
  andThen(function() {
    assert.equal(currentPath(), 'protected.investigate.investigate-events.index', 'correct path was redirected into.');
  });
});

// deeply nested URLs do not redirect back
test('investigate\'s query results page does not redirect back to investigate-events.index', function(assert) {
  visit('/investigate/events/query/0/0/0');
  andThen(function() {
    assert.equal(currentURL(), '/investigate/events/query/0/0/0', 'correct path remains.');
  });
});
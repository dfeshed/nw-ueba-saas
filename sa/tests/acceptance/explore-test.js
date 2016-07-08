import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | explore', {
  // After each test, destroy the MockServer instances we've created (if any), so that the next test will not
  // throw an error when it tries to re-create them.
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('visiting /do/explore and check DOM', function(assert) {
  visit('/do/explore');

  andThen(function() {
    assert.equal(currentPath(), 'protected.explore');
  });

});

import { skip } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | respond', {
  // After each test, destroy the MockServer instances we've created (if any), so that the next test will not
  // throw an error when it tries to re-create them.
  afterEach() {
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

skip('visiting /do/respond and check DOM', function(assert) {
  assert.expect(2);

  visit('/do/respond');

  andThen(function() {
    assert.equal(currentPath(), 'protected.respond');
  });

});

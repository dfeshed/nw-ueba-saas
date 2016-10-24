import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import teardownSockets from 'sa/tests/helpers/teardown-sockets';

moduleForAcceptance('Acceptance | monitor', {
  afterEach: teardownSockets
});

test('visiting /do/monitor and check DOM', function(assert) {
  visit('/do/monitor');

  andThen(function() {
    assert.equal(currentPath(), 'protected.monitor');
  });

});

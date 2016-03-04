import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | monitor');

test('visiting /do/monitor and check DOM', function(assert) {
  visit('/do/monitor');

  andThen(function() {
    assert.equal(currentPath(), 'protected.monitor');
  });

});

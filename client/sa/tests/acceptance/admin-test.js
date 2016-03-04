import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | admin');

test('visiting /do/admin and check DOM', function(assert) {
  visit('/do/admin');

  andThen(function() {
    assert.equal(currentPath(), 'protected.admin');
  });
});

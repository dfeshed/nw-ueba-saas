import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | explore');

test('visiting /do/explore and check DOM', function(assert) {
  visit('/do/explore');

  andThen(function() {
    assert.equal(currentPath(), 'protected.explore');
  });

});

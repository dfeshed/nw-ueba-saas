import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | Configure Route', {});

test('should have an active Configure tab in the navigation header', function(assert) {
  visit('/configure');
  andThen(() => {
    assert.equal(find('.rsa-header-nav-configure.active').length, 1);
  });
});

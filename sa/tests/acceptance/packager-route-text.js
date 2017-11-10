import { test } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | Packager Route', {});

test('should display packager content with application header', function(assert) {
  visit('/packager');
  andThen(() => {
    assert.equal(find('.rsa-application-header:visible').length, 1);
    assert.equal(find('.packager-container__body').length, 1);
    assert.equal(find('.packager-information__header').length, 1);
  });
});

test('should hide the application header if route is included in iframe', function(assert) {
  visit('/packager?iframedIntoClassic=true');
  andThen(() => {
    assert.equal(find('.rsa-application-header:visible').length, 0);
    assert.equal(find('.packager-container__body').length, 1);
    assert.equal(find('.packager-information__header').length, 1);
  });
});

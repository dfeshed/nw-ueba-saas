import { test } from 'qunit';
import moduleForAcceptance from '../helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | Hosts Scan Route', {});

test('should display schedule page content with application header', function(assert) {
  visit('/configure/hosts-scan');
  andThen(() => {
    assert.equal(find('.rsa-nav-tab-group:visible').length, 1);
    assert.equal(find('.hosts-scan-configure-container').length, 1);
  });
});

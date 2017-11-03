import $ from 'jquery';
import { test } from 'qunit';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import teardownSockets from 'sa/tests/helpers/teardown-sockets';

moduleForAcceptance('Acceptance | theme test', {
  beforeEach() {
    localStorage.setItem('reduxPersist:global', JSON.stringify({
      preferences: {
        theme: 'LIGHT'
      }
    }));
  },
  afterEach() {
    localStorage.removeItem('reduxPersist:global');
    $('body').removeClass('light-theme').addClass('dark-theme');
    teardownSockets.apply(this);
  }
});

test('theme will rehydrate from local storage on boot', function(assert) {
  assert.expect(4);
  visit('/');
  andThen(() => {
    assert.ok($('body').hasClass('dark-theme'));
    assert.notOk($('body').hasClass('light-theme'));
  });
  waitFor(() => $('body').hasClass('light-theme')).then(() => {
    assert.ok($('body').hasClass('light-theme'));
    assert.notOk($('body').hasClass('dark-theme'));
  });
});

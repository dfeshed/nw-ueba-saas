import $ from 'jquery';
import { skip } from 'qunit';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import moduleForLogin from 'sa/tests/helpers/module-for-login';

moduleForLogin('Acceptance | theme test', {
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
  }
});

skip('theme will rehydrate from local storage on boot', function(assert) {
  assert.expect(9);

  assert.ok($('body').hasClass('dark-theme'));
  assert.notOk($('body').hasClass('light-theme'));

  visit('/');
  andThen(() => {
    assert.equal(currentURL(), '/login');
    assert.equal(find('[test-id=loginButton] button').attr('disabled'), 'disabled');
    return waitFor(() => $('body').hasClass('light-theme')).then(() => {
      assert.ok($('body').hasClass('light-theme'));
      assert.notOk($('body').hasClass('dark-theme'));
    });
  });
  fillIn('[test-id=loginUsername] input', 'admin');
  fillIn('[test-id=loginPassword] input', 'netwitness');
  andThen(() => {
    assert.equal(find('[test-id=loginButton] button').attr('disabled'), undefined);
  });
  click('[test-id=loginButton] button');
  andThen(() => {
    return waitFor(() => $('body').hasClass('dark-theme')).then(() => {
      assert.ok($('body').hasClass('dark-theme'));
      assert.notOk($('body').hasClass('light-theme'));
    });
  });
});

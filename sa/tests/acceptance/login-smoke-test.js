import { test } from 'qunit';
import moduleForLogin from 'sa/tests/helpers/module-for-login';

moduleForLogin('Acceptance | login smoke test', {
  beforeEach() {
    localStorage.removeItem('rsa-post-auth-redirect');
  }
});

test('login authenticates user without classic redirect', function(assert) {
  assert.expect(4);

  visit('/');
  andThen(() => {
    assert.equal(currentURL(), '/login');
    assert.equal(find('[test-id=loginButton] button').attr('disabled'), 'disabled');
  });
  fillIn('[test-id=loginUsername] input', 'admin');
  fillIn('[test-id=loginPassword] input', 'netwitness');
  andThen(() => {
    assert.equal(find('[test-id=loginButton] button').attr('disabled'), undefined);
  });
  click('[test-id=loginButton] button');
  andThen(() => {
    assert.notEqual(currentURL(), '/login');
  });
});

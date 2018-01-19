import { skip } from 'qunit';
import moduleForLogin from 'sa/tests/helpers/module-for-login';

moduleForLogin('Acceptance | not found redirect test', {
  beforeEach() {
    localStorage.removeItem('rsa-post-auth-redirect');
  }
});

skip('invalid url will redirect to not-found without error', function(assert) {
  assert.expect(3);

  visit('/respond/incidents');
  andThen(() => {
    assert.equal(currentURL(), '/login');
  });
  fillIn('[test-id=loginUsername] input', 'admin');
  fillIn('[test-id=loginPassword] input', 'netwitness');
  click('[test-id=loginButton] button');
  andThen(() => {
    assert.equal(currentURL(), '/respond/incidents');
  });
  visit('/investigate/eventzzz');
  andThen(() => {
    assert.equal(currentURL(), '/not-found');
  });
});

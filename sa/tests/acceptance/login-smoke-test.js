import { skip } from 'qunit';
import moduleForLogin from 'sa/tests/helpers/module-for-login';

moduleForLogin('Acceptance | login smoke test', {
  beforeEach() {
    localStorage.removeItem('rsa-post-auth-redirect');
    localStorage.removeItem('rsa-i18n-default-locale');
  }
});

skip('locale defaults to english regardless of users language preference', function(assert) {
  assert.expect(3);

  visit('/');
  andThen(() => {
    assert.equal(currentURL(), '/login');
  });
  fillIn('[test-id=loginUsername] input', 'admin');
  fillIn('[test-id=loginPassword] input', 'netwitness');
  click('[test-id=loginButton] button');
  andThen(() => {
    assert.equal(currentURL(), '/respond/incidents');
    const locale = this.application.__container__.lookup('service:i18n').get('locale');
    assert.equal(locale, 'en');
  });
});

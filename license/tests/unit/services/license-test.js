import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';

const BANNER_DISMISSED_KEY = 'rsa-license-banner-dismissed';

module('Unit | Services | license', function(hooks) {

  setupTest(hooks);

  hooks.afterEach(function() {
    sessionStorage.removeItem(BANNER_DISMISSED_KEY);
  });

  test('should return the license compliance', async function(assert) {
    const service = this.owner.lookup('service:license');
    const compliance = await service.getCompliance();
    assert.ok(compliance, 'License compliance must not be null');
    assert.ok(!compliance.compliant, 'Compliant flag expected to be false');
    assert.equal(compliance.compliances.length, 2, 'Incorrect number of compliances in the list');
  });

  test('should set the banner dismissed key in session storage', async function(assert) {
    const service = this.owner.lookup('service:license');
    service.setBannerDismissed();
    assert.equal(sessionStorage.getItem(BANNER_DISMISSED_KEY), 'true', 'Banner dismissed key should be set in session storage');
  });

  test('should remove the banner dismissed key from session storage', async function(assert) {
    const service = this.owner.lookup('service:license');
    service.resetBannerDismissed();
    assert.ok(!sessionStorage.getItem(BANNER_DISMISSED_KEY), 'Banner dismissed key should be removed from session storage');
  });

  test('should check the banner dismissed key value in session storage', async function(assert) {
    const service = this.owner.lookup('service:license');
    assert.ok(!service.isBannerDismissed(), 'isBannerDismissed must return false since session storage does not have banner reset key');
    sessionStorage.setItem(BANNER_DISMISSED_KEY, 'true');
    assert.ok(service.isBannerDismissed(), 'isBannerDismissed must return true since session storage does not have banner reset key');
  });
});

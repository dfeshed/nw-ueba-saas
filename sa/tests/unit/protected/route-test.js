import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { Promise } from 'rsvp';
import { next } from '@ember/runloop';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';

// feature flag data looks like:
//
// {
//   code: 0,
//   data: {
//     'rsa.usm': true,
//     'rsa.usm.featureOne,
//     'rsa.usm.featureTwo
//   }
// }

const sessionStorageClear = () => {
  sessionStorage.clear();
  return new Promise((resolve) => {
    waitFor(() => sessionStorage.getItem('features.rsaUsm') === null).then(() => {
      next(null, resolve);
    });
  });
};

module('Unit | Route | protected', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('route', 'request', 'service:request', 'service:features');
    return sessionStorageClear();
  });

  hooks.afterEach(function() {
    return sessionStorageClear();
  });

  test('should fetch Source Management (USM) feature flags and store in features service & sessionStorage', async function(assert) {
    const features = this.owner.lookup('service:features');
    const route = this.owner.lookup('route:protected');
    route.set('router.currentRouteName', 'protected');

    // disabled by default
    let isRsaUsmEnabled = features.isEnabled('rsa.usm');
    assert.equal(isRsaUsmEnabled, false, 'feature rsa.usm is disabled by default');
    // not in sessionStorage by default
    let isRsaUsmInSessionStorage = sessionStorage.getItem('features.rsaUsm');
    assert.equal(isRsaUsmInSessionStorage, null, 'features.rsaUsm is not in sessionStorage by default');

    const promise = route.getSourceManagementFeatures();
    await promise;

    // enabled by the service call
    isRsaUsmEnabled = features.isEnabled('rsa.usm');
    assert.equal(isRsaUsmEnabled, true, 'feature rsa.usm is enabled by the service call');
    // set in sessionStorage by the service call
    isRsaUsmInSessionStorage = JSON.parse(sessionStorage.getItem('features.rsaUsm'));
    assert.equal(isRsaUsmInSessionStorage, true, 'features.rsaUsm is set in sessionStorage by the service call');
  });
});

import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | Service | features', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    // reset/empty the features
    this.owner.lookup('service:features').setFeatureFlags({}, true);
  });

  test('setFeatureFlags', function(assert) {
    const features = this.owner.lookup('service:features');

    features.setFeatureFlags({ 'rsa.featOne': true, 'rsa.featTwo': false });
    assert.equal(features.hasFeatureFlag('rsa.featOne'), true, 'rsa.featOne should be set');
    assert.equal(features.isEnabled('rsa.featOne'), true, 'rsa.featOne should be enabled');
    assert.equal(features.hasFeatureFlag('rsa.featTwo'), true, 'rsa.featTwo should be set');
    assert.equal(features.isEnabled('rsa.featTwo'), false, 'rsa.featTwo should be disabled');

    features.setFeatureFlags({}, true);
    assert.equal(features.hasFeatureFlag('rsa.featOne'), false, 'rsa.featOne should NOT be set');
    assert.equal(features.isEnabled('rsa.featOne'), false, 'rsa.featOne should be disabled');
    assert.equal(features.hasFeatureFlag('rsa.featTwo'), false, 'rsa.featTwo should NOT be set');
    assert.equal(features.isEnabled('rsa.featTwo'), false, 'rsa.featTwo should be disabled');
  });

  test('hasFeatureFlag', function(assert) {
    const features = this.owner.lookup('service:features');

    assert.equal(features.hasFeatureFlag('rsa.someFeatureFlag'), false, 'hasFeatureFlag is false by default when features doesn\'t have the given feature flag');

    features.setFeatureFlags({ 'rsa.someFeatureFlag': true });
    assert.equal(features.hasFeatureFlag('rsa.someFeatureFlag'), true, 'hasFeatureFlag is true if the given feature flag is added');

    features.setFeatureFlags({ 'rsa.someFeatureFlag': false });
    assert.equal(features.hasFeatureFlag('rsa.someFeatureFlag'), true, 'hasFeatureFlag is true if the given feature flag is added');
  });

  test('isEnabled', function(assert) {
    const features = this.owner.lookup('service:features');

    assert.equal(features.isEnabled('rsa.someFeatureFlag'), false, 'isEnabled is false by default when features doesn\'t have the given feature flag');

    features.setFeatureFlags({ 'rsa.someFeatureFlag': true });
    assert.equal(features.isEnabled('rsa.someFeatureFlag'), true, 'isEnabled is true if feature is set to true');

    features.setFeatureFlags({ 'rsa.someFeatureFlag': false });
    assert.equal(features.isEnabled('rsa.someFeatureFlag'), false, 'isEnabled is false if feature is set to false');
  });

  test('enable', function(assert) {
    const features = this.owner.lookup('service:features');

    features.setFeatureFlags({ 'rsa.someFeatureFlag': false });
    features.enable('rsa.someFeatureFlag');
    assert.equal(features.isEnabled('rsa.someFeatureFlag'), true, 'isEnabled is true after calling enable(flag)');
  });

  test('disable', function(assert) {
    const features = this.owner.lookup('service:features');

    features.setFeatureFlags({ 'rsa.someFeatureFlag': true });
    features.disable('rsa.someFeatureFlag');
    assert.equal(features.isEnabled('rsa.someFeatureFlag'), false, 'isEnabled is false after calling disable(flag)');
  });

});

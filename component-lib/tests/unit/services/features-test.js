import { module, test } from 'ember-qunit';

module('service:features', 'Unit | Service | features', {
  needs: []
}, function() {
  test('isEnabled', function(assert) {
    const features = this.subject();
    features.setup(undefined);
    assert.equal(features.isEnabled('endpoint-fusion'), false, 'Feature is false if Features is undefined');

    features.setup({});
    assert.equal(features.isEnabled('endpoint-fusion'), false, 'Feature is false if Feature on Features is undefined');

    features.setup({ 'endpoint-fusion': true });
    assert.equal(features.isEnabled('endpoint-fusion'), true, 'Feature is true if feature is set to true');

    features.setup({ 'endpoint-fusion': false });
    assert.equal(features.isEnabled('endpoint-fusion'), false, 'Feature is false if feature is set to false');
  });
});

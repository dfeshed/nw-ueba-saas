import wait from 'ember-test-helpers/wait';
import hbs from 'htmlbars-inline-precompile';
import waitFor from 'sa/tests/helpers/wait-for';
import { moduleForComponent, test } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Service from 'ember-service';

const accessControl = Service.extend({
  hasReconAccess: true
});

moduleForComponent('recon-investigate-wrapper', 'Integration | Component | recon investigate wrapper', {
  integration: true,
  beforeEach() {
    this.register('service:access-control', accessControl);

    this.inject.service('i18n');
    this.registry.injection('component:recon-event-actionbar/export-packet', 'i18n', 'service:i18n');
    this.registry.injection('component:recon-event-detail/single-text', 'i18n', 'service:i18n');
    this.inject.service('access-control');
    this.inject.service('redux');
    initialize(this);
  }
});

test('recon container will hide header data when toggle header clicked', function(assert) {
  assert.expect(3);

  this.set('eventId', '5');
  this.set('endpointId', '555d9a6fe4b0d37c827d402e');

  this.render(hbs`{{recon-investigate-wrapper eventId=eventId endpointId=endpointId}}`);

  const headerSelector = '[test-id=headerData]';
  const toggleSelector = '[test-id=toggleHeader]';

  return wait().then(() => {
    assert.equal(this.$(toggleSelector).length, 1);
    assert.equal(this.$(headerSelector).length, 1);
    return waitFor(() => this.$(toggleSelector).trigger('click'))().then(() => {
      assert.equal(this.$(headerSelector).length, 0);
    });
  });
});

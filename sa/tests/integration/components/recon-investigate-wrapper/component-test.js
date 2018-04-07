import hbs from 'htmlbars-inline-precompile';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { localStorageClear } from 'sa/tests/helpers/wait-for';
import { render, click, findAll, settled } from '@ember/test-helpers';
import Service from '@ember/service';

const accessControl = Service.extend({
  hasReconAccess: true
});

module('Integration | Component | recon investigate wrapper', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.register('service:access-control', accessControl);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    return localStorageClear();
  });

  test('recon container will hide header data when toggle header clicked', async function(assert) {
    assert.expect(3);

    this.set('eventId', '5');
    this.set('endpointId', '555d9a6fe4b0d37c827d402e');

    await render(hbs`{{recon-investigate-wrapper eventId=eventId endpointId=endpointId}}`);

    const headerSelector = '[test-id=headerData]';
    const toggleSelector = '[test-id=toggleHeader]';

    await settled();
    assert.equal(findAll(toggleSelector).length, 1);
    assert.equal(findAll(headerSelector).length, 1);

    await click(toggleSelector);
    assert.equal(findAll(headerSelector).length, 0);
  });

});

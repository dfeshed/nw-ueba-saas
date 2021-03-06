import hbs from 'htmlbars-inline-precompile';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, settled, waitUntil } from '@ember/test-helpers';
import Service from '@ember/service';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import LinkToExternalComponent from 'ember-engines/components/link-to-external-component';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const accessControl = Service.extend({
  hasReconAccess: true
});

module('Integration | Component | recon investigate wrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.register('service:access-control', accessControl);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('component:link-to-external', LinkToExternalComponent);
  });

  test('recon container is rendered', async function(assert) {
    assert.expect(2);

    assert.timeout(20000);
    const done = assert.async();
    this.set('eventId', '5');
    this.set('endpointId', '555d9a6fe4b0d37c827d402e');

    await render(hbs`{{recon-investigate-wrapper eventId=eventId endpointId=endpointId}}`);

    return settled().then(async() => {
      return waitUntil(() => findAll('.recon-event-content .scroll-box').length > 0, { timeout: Infinity }).then(() => {
        assert.equal(findAll('.recon-standalone-container').length, 1, 'renders recon-investigate-wrapper');
        assert.equal(findAll('.recon-container').length, 1, 'renders recon-container');
        done();
      });
    });
  });
});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Service from '@ember/service';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import rsvp from 'rsvp';

const contextStub = Service.extend({
  metas: () => {
    return new rsvp.Promise((resolve) => resolve({}));
  }
});

module('Integration | Component | context-panel', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:context', contextStub);
  });

  test('Test context panel should display', async function(assert) {
    this.set('entityId', '1.1.1.1');
    this.set('entityType', 'IP');
    await render(hbs`{{context-panel entityId=entityId entityType=entityType }}`);

    return waitFor('.rsa-nav-tab-group').then(() => {
      assert.equal(findAll('.rsa-nav-tab').length, 6, 'We should get all 5 data sources for meta ip');
    });
  });

  test('Test context panel should not close on clicking other tab', async function(assert) {
    this.set('entityId', '1.1.1.1');
    this.set('entityType', 'IP');
    await render(hbs`{{context-panel entityId=entityId entityType=entityType }}`);

    return waitFor('.rsa-nav-tab-group').then(() => {
      assert.equal(findAll('.rsa-nav-tab').length, 6, 'Should render all 6 data sources for meta ip');
      click('.rsa-icon-flag-square-2-filled');
      assert.equal(findAll('.rsa-nav-tab').length, 6, 'Should not close panel onclicking another data source');
      click('.rsa-icon-alarm-sound-filled');
      assert.equal(findAll('.rsa-nav-tab').length, 6, 'Should not close panel onclicking another data source');
    });
  });
});

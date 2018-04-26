import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Service from '@ember/service';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import rsvp from 'rsvp';

const contextStub = Service.extend({
  metas: () => {
    return new rsvp.Promise((resolve) => resolve({}));
  }
});

module('Integration | Component | context-panel/pivotToArcher', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:context', contextStub);
  });

  test('Test Pivot to Archer rendered for IP', async function(assert) {
    this.set('entityId', '1.1.1.1');
    this.set('entityType', 'IP');
    await render(hbs`{{context-panel entityId=entityId entityType=entityType }}`);

    return waitFor('.rsa-nav-tab-group').then(() => {
      assert.equal(findAll('div.rsa-context-panel__linkButton').length, 1, 'Pivot to Archer link is displayed for IP');
    });
  });

  test('Pivot to Archer link is available for MAC_ADDRESS', async function(assert) {
    this.setProperties({
      entityType: 'MAC_ADDRESS',
      entityId: '00:50:56:BA:60:18'
    });
    await render(hbs`{{context-panel entityId=entityId entityType=entityType }}`);
    return waitFor('.rsa-nav-tab-group').then(() => {
      assert.equal(findAll('div.rsa-context-panel__linkButton').length, 1, 'Pivot to Archer link is displayed for MAC_ADDRESS');
    });
  });

});

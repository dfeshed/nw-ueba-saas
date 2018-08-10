import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/risk-properties', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    this.set('activeTab', 'CRITICAL');
    await render(hbs`{{endpoint/risk-properties activeAlertTab=activeTab}}`);

    assert.equal(findAll('.risk-properties').length, 1, 'risk properties is rendered');
    assert.equal(findAll('.risk-properties .rsa-nav-tab').length, 4, '4 tabs are present');

  });
});

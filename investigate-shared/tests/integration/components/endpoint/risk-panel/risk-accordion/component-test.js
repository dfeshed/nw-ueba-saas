import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/risk-panel/risk-accordion', function(hooks) {
  setupRenderingTest(hooks);

  test('Risk accordion is rendered', async function(assert) {

    assert.equal(this.element.textContent.trim(), '');
    this.set('entry', {
      alertId: '5afcffbedb7a8b75269a0040'
    });

    await render(hbs`
      {{#endpoint/risk-panel/risk-accordion animate=true entry=entry}}
      Content
      {{/endpoint/risk-panel/risk-accordion}}`);
    assert.equal(this.element.textContent.trim(), 'Content', 'Content is displayed');
    assert.equal(findAll('.respond-link').length, 1, 'Alert link is present');
  });
});

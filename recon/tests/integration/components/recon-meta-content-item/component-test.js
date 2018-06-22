import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll } from '@ember/test-helpers';

module('Integration | Component | recon-meta-content-item', function(hooks) {
  setupRenderingTest(hooks);
  test('meta item name and value rendered', async function(assert) {
    this.set('item', ['test-name', 'test-value']);
    this.set('metaFormatMap', { 'test-name': 'Text' });
    await this.render(hbs `{{recon-meta-content-item item=item metaFormatMap=metaFormatMap}}`);
    assert.equal(findAll('.meta-name')[0].innerText, 'TEST-NAME');
    assert.equal(findAll('.meta-value')[0].innerText.trim(), 'test-value');
  });

  test('Test should have all attribute for CH integration', async function(assert) {
    this.set('item', ['test-name', 'test-value']);
    this.set('metaFormatMap', { 'test-name': 'Text' });
    await this.render(hbs `{{recon-meta-content-item item=item metaFormatMap=metaFormatMap}}`);
    const [spanObj] = findAll('span.entity');
    assert.ok(spanObj, 'Entity class should be added');
    assert.equal(spanObj.getAttribute('data-entity-id'), 'test-value', 'Should have entity id set');
    assert.equal(spanObj.getAttribute('data-meta-key'), 'test-name', 'Should have meta key set');
  });
});
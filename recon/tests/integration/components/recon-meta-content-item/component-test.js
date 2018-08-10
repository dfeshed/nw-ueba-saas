import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, triggerEvent } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | recon-meta-content-item', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('meta item name and value rendered', async function(assert) {
    this.set('item', ['test-name', 'test-value']);
    this.set('metaFormatMap', { 'test-name': 'Text' });
    await render(hbs `{{recon-meta-content-item item=item metaFormatMap=metaFormatMap}}`);
    assert.equal(findAll('.meta-name')[0].innerText, 'TEST-NAME');
    assert.equal(findAll('.meta-value')[0].innerText.trim(), 'test-value');
  });

  test('Test should have all attribute for CH integration', async function(assert) {
    this.set('item', ['test-name', 'test-value']);
    this.set('metaFormatMap', { 'test-name': 'Text' });
    await render(hbs `{{recon-meta-content-item item=item metaFormatMap=metaFormatMap}}`);
    const [spanObj] = findAll('span.entity');
    assert.ok(spanObj, 'Entity class should be added');
    assert.equal(spanObj.getAttribute('data-entity-id'), 'test-value', 'Should have entity id set');
    assert.equal(spanObj.getAttribute('data-meta-key'), 'test-name', 'Should have meta key set');
  });

  test('show tooltip for endpoint event lengthy meta', async function(assert) {
    const endpointData = [{
      'charset': 'UTF-8',
      'contentDecoded': true,
      'firstPacketId': 1,
      'firstPacketTime': 1485792552870,
      'text': 'param.dst=test-value test-value test-value test-value test-value 0000000'
    }];
    this.set('isEndpointEvent', true);
    this.set('hasTextContent', true);
    this.set('renderedText', endpointData);

    this.set('item', ['param.dst', 'test-value test-value']);
    this.set('metaFormatMap', { 'param.dst': 'Text' });
    await render(hbs`{{recon-meta-content-item
                       item=item
                       isEndpointEvent=isEndpointEvent
                       hasTextContent=hasTextContent
                       renderedText=renderedText
                       metaFormatMap=metaFormatMap}}
    `);
    document.querySelector('.tooltip-text').setAttribute('style', 'width:100px');
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(findAll('.ember-tether').length, 1, 'Tool tip is rendered');
    assert.equal(find('.ember-tether .tool-tip-value').textContent.trim(), 'test-value test-value test-value test-value test-value 0000000');
    await triggerEvent('.tooltip-text', 'mouseout');
    assert.equal(findAll('.ember-tether .tool-tip-value').length, 0, 'Tool tip is hidden');
  });
});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll } from '@ember/test-helpers';

module('Integration | Component | recon-event-header-item', function(hooks) {
  setupRenderingTest(hooks);

  test('header item name and value render', async function(assert) {
    this.set('name', 'nwService');
    this.set('value', 'concentrator');
    await this.render(hbs `{{recon-event-header-item name=name value=value}}`);
    assert.equal(this.$('.name').first().text().trim(), 'NW Service');
    assert.equal(this.$('.value').first().text().trim(), 'concentrator');
  });

  test('Context menu trigger renders for ordinary value', async function(assert) {
    this.set('name', 'sessionId');
    this.set('value', '123');

    this.set('key', 'sessionid');
    this.set('metaFormatMap', { sessionid: 'Text' });

    await this.render(hbs `{{recon-event-header-item name=name value=value key=key metaFormatMap=metaFormatMap}}`);
    assert.equal(findAll('span.content-context-menu').length, 1, 'Context menu trigger should be rendered');
  });

  test('Two separate context menu triggers render for IP and port', async function(assert) {
    this.set('name', 'source');
    this.set('value', '10.1.1.1 : 9090');
    this.set('key', 'ip.src : port.src');

    await this.render(hbs `{{recon-event-header-item name=name value=value key=key}}`);
    assert.equal(findAll('span.content-context-menu').length, 2, 'Separate context menu triggers should be rendered for ip and port');
  });

  test('Context menu trigger does not render when key is null', async function(assert) {
    this.set('name', 'sessionId');
    this.set('value', '123');

    await this.render(hbs `{{recon-event-header-item name=name value=value}}`);
    assert.equal(findAll('span.content-context-menu').length, 0, 'Context menu trigger should not be rendered');
  });

  test('test entity attributes added for context integration', async function(assert) {
    this.set('name', 'sessionId');
    this.set('value', '123');

    this.set('key', 'sessionid');
    this.set('metaFormatMap', { sessionid: 'Text' });

    await this.render(hbs `{{recon-event-header-item name=name value=value key=key metaFormatMap=metaFormatMap}}`);
    const [spanObj] = findAll('span.entity');
    assert.ok(spanObj, 'Entity class should be added');
    assert.equal(spanObj.getAttribute('data-entity-id'), '123', 'Should have entity id set');
    assert.equal(spanObj.getAttribute('data-meta-key'), 'sessionid', 'Should have meta key set');
  });
});
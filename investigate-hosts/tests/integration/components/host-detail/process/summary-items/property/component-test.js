import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

const item = {
  field: 'fileName',
  label: 'investigateHosts.process.fileName',
  value: 'ntoskrnl.exe',
  format: 'format1'
};


module('Integration | Component | endpoint host-detail/process/summary-items/property', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('this is to test if property value is populated', async function(assert) {
    this.set('item', item);
    await render(hbs`{{host-detail/process/summary-items/property item=item}}`);
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.header-item .value').length, 1, 'property value populated for corresponding summary item passed');
    });
  });

  test('test for hasBlock', async function(assert) {
    this.set('item', item);
    await render(hbs`
      {{#host-detail/process/summary-items/property item=item as |label value|}}
        <div class="value">{{label}}</div>
      {{/host-detail/process/summary-items/property}}
    `);
    return settled().then(() => {
      assert.equal(document.querySelector('.header-item .value').textContent.trim(), 'investigateHosts.process.fileName', 'hasBlock property values populated for corresponding summary items passed');
    });
  });

  test('this is to test the custom class passed with item', async function(assert) {
    const newItem = {
      field: 'fileName',
      label: 'investigateHosts.process.fileName',
      value: 'ntoskrnl.exe',
      format: 'format1',
      cssClass: 'custom-class'
    };
    this.set('item', newItem);
    await render(hbs`{{host-detail/process/summary-items/property item=item}}`);
    return settled().then(() => {
      assert.equal(document.querySelector('.header-item').classList.contains('custom-class'), true, 'has custom class passed with item');
    });
  });
  test('external link should add to the file name', async function(assert) {
    const newItem = {
      field: 'fileName',
      label: 'investigateHosts.process.fileName',
      value: 'ntoskrnl.exe',
      format: 'format1',
      cssClass: 'custom-class'
    };
    this.set('item', newItem);
    await render(hbs`{{host-detail/process/summary-items/property item=item}}`);
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.value a').length, 1, 'link added to file name');
    });
  });
});
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/file-custom-row', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders data table row', async function(assert) {
    this.set('selections', []);
    this.set('item', { id: '123' });
    await render(hbs`{{endpoint/file-custom-row item=item selections=selections}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 1, 'Table row is rendered.');
  });

  test('context menu is rendered', async function(assert) {
    this.set('selections', []);
    this.set('item', { id: '123' });
    await render(hbs`{{endpoint/file-custom-row item=item selections=selections}}`);
    assert.equal(findAll('.content-context-menu').length, 1, 'Context menu is rendered.');
  });

  test('row is getting high lighted', async function(assert) {
    this.set('selections', [{ id: '123456' }, { id: '345' }]);
    this.set('item', { id: '123456' });
    await render(hbs`{{endpoint/file-custom-row item=item selections=selections}}`);
    assert.equal(findAll('.is-row-checked').length, 1, 'row is high lighted');
  });
});

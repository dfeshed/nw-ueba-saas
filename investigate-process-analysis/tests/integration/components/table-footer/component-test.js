import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | table footer', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Should show count of total items', async function(assert) {
    const count = 4;
    const selections = 1;
    const items = ['Harp', 'WIN10x64', 'WIN8x64', 'server.local', 'CentOS'];
    this.set('count', count);
    this.set('itemsLength', items.length);
    this.set('selections', selections);
    await render(
      hbs`{{table-footer total=itemsLength index=count selectedItems=selections}}`
    );
    const expected1 = `Showing ${count} out of ${items.length}`;
    const expected2 = `| ${selections} selected`;
    assert.equal(find('div.footer-info').textContent.trim().includes(expected1), true);
    assert.equal(find('div.footer-info').textContent.trim().includes(expected2), true);
  });

  test('Selection message not displayed if selection is -1', async function(assert) {
    const count = 4;
    const selections = 1;
    const items = ['Harp', 'WIN10x64', 'WIN8x64', 'server.local', 'CentOS'];
    this.set('count', count);
    this.set('itemsLength', items.length);
    this.set('selections', -1);
    await render(
      hbs`{{table-footer total=itemsLength index=count selectedItems=selections}}`
    );
    const expected1 = `Showing ${count} out of ${items.length}`;
    const expected2 = `| ${selections} selected`;
    assert.equal(find('div.footer-info').textContent.trim().includes(expected1), true);
    assert.equal(find('div.footer-info').textContent.trim().includes(expected2), false, '');
  });
});

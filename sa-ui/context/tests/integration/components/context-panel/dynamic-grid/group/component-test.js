import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, triggerEvent } from '@ember/test-helpers';

const data = ['Test1', 'Test2', 'Test3'];
const index = 1;
const title = 'context.Business.Unit';

module('Integration | Component | context-panel/grid', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {

    this.set('data', data);
    this.set('title', title);
    this.set('index', index);
    await render(hbs`{{context-panel/dynamic-grid/group groupData=data title=title index=index}}`);
    assert.ok(findAll('.rsa-context-panel__grid__host-details__tetheredPanel')[0].textContent.trim(), 3);
  });

  test('Tethered panel is rendered', async function(assert) {
    this.set('data', data);
    this.set('title', title);
    this.set('index', index);
    await render(hbs`{{context-panel/dynamic-grid/group groupData=data title=title index=index}}`);
    await triggerEvent('.rsa-context-panel__grid__host-details__tetheredPanel', 'mouseover');
    assert.ok(findAll('.rsa-context-panel__grid__host-details__groupdata').length, 1);
  });

});
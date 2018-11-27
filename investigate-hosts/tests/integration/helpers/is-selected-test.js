import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';


module('Integration | Component | host-detail/process/process-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  test('it renders is-selected helper', async function(assert) {
    this.set('selectedList', [ { id: 0 }, { id: 1 } ]);
    this.set('item', { id: 2 });
    await render(hbs`{{is-selected selectedList item}}`);
    assert.equal(findAll('.ember-view')[0].textContent.trim(), 'false', 'is-selected should false');

    this.set('item', { id: 1 });
    await render(hbs`{{is-selected selectedList item}}`);
    assert.equal(findAll('.ember-view')[0].innerText, 'true', 'is-selected should true');
  });

  test('it renders is-selected helper, when the item has a pid instead of an id', async function(assert) {
    this.set('selectedList', [ { pid: 0 }, { pid: 1 } ]);
    this.set('item', { pid: 2 });
    await render(hbs`{{is-selected selectedList item}}`);
    assert.equal(findAll('.ember-view')[0].textContent.trim(), 'false', 'is-selected returns false');

    this.set('item', { pid: 1 });
    await render(hbs`{{is-selected selectedList item}}`);
    assert.equal(findAll('.ember-view')[0].innerText, 'true', 'is-selected returns true');
  });

});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | tree-view-value', function(hooks) {
  setupRenderingTest(hooks);

  test('it displays a normal value for a non-size node', async function(assert) {
    this.set('node', {
      path: '/sys/tasks/message',
      name: 'message',
      value: 'Message',
      nodeType: 3098423767072768
    });
    await render(hbs`{{tree-view-value node=node}}`);
    assert.strictEqual(this.element.textContent.trim(), 'Message');
    assert.notOk(find('.ngcoreui-memsize'));
  });

  test('it displays a memsize component for a size node', async function(assert) {
    this.set('node', {
      path: '/sys/stats/memory.process',
      name: 'memory.process',
      value: '1244831744',
      nodeType: 598134325510176
    });
    await render(hbs`{{tree-view-value node=node}}`);
    assert.ok(find('.ngcoreui-memsize'));
  });
});

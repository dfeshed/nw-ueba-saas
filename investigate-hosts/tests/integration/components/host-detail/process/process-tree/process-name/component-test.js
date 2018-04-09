import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from '../../../../../../helpers/engine-resolver';

module('Integration | Component | host-detail/process/process-tree/process-name', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  test('should render process name', async function(assert) {
    assert.expect(1);
    this.set('item', { name: 'cmd.exe' });
    this.set('index', 0);
    await render(hbs`{{host-detail/process/process-tree/process-name item=item index=index}}`);
    assert.equal(find('.process-name-column').textContent.trim(), 'cmd.exe', 'process name is rendered');
  });
});
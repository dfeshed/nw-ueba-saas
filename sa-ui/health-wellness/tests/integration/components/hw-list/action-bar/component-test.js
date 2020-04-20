import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

module('Integration | Component | action-bar', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs`{{hw-list/action-bar}}`);

    assert.equal(findAll('.action-bar').length, 1, 'health wellness action bar is rendered');
    assert.equal(find('.action-bar .notification-button').textContent.trim(), 'Notification', 'notification button rendered');
    assert.equal(find('.action-bar .suppression-policy').textContent.trim(), 'Suppression Policy', 'notification button rendered');
    assert.equal(findAll('.action-bar .rsa-button-group').length, 1, 'Pivot to Kibana dropdown health wellness action bar is rendered');
    assert.equal(findAll('.action-bar .rsa-button-menu li').length, 2, 'Drop down list exists');
    assert.equal(findAll('.action-bar .rsa-button-menu li')[0].textContent.trim(), 'Kibana Dashboard');
    assert.equal(findAll('.action-bar .rsa-button-menu li')[1].textContent.trim(), 'Kibana Alert');
  });
});
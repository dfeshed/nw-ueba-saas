import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | context-panel/header/title', function(hooks) {
  setupRenderingTest(hooks);

  test('Header title should be shown when no error is occured', async function(assert) {

    this.set('headerTitle', 'Alerts');

    this.set('lookupKey', 'test-alert');

    await render(hbs`{{context-panel/header/title headerTitle=headerTitle lookupKey=lookupKey}}`);

    assert.equal(find('.rsa-context-panel-header__title span').textContent.trim(), 'Alerts :', 'headerTitle is displayed in header title');

    assert.equal(find('.rsa-context-panel-header__title__lookup').textContent.trim(), 'test-alert', 'lookupKey is displayed in header title');
  });

  test('Header title is empty when some error occured.', async function(assert) {

    this.set('errorMessage', 'test-error');

    this.set('headerTitle', 'Alerts');

    this.set('lookupKey', 'test-alert');

    await render(hbs`{{context-panel/header/title headerTitle=headerTitle errorMessage=errorMessage lookupKey=lookupKey}}`);

    assert.notOk(find('.rsa-context-panel-header__title').textContent.trim());
  });
});

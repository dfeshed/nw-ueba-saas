import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, findAll, find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | context-panel/header/icons', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {

    await render(hbs`{{context-panel/header/icons}}`);

    assert.equal(findAll('.rsa-context-panel-header__panel-close').length, 1, 'close context panel icon is present');

    assert.equal(findAll('.rsa-form-button').length, 1, 'help icon is present');

    await click(find('.rsa-context-panel-header__panel-close'));

  });
});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa application layout panel', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-application-layout-panel}}`);
    assert.equal(findAll('vbox.rsa-application-layout-panel').length, 1);
  });
});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-content-section-header', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-content-section-header label='foo'}}`);
    const header = findAll('.rsa-content-section-header').length;
    assert.equal(header, 1);
  });
});
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-dashboard', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the rsa-dashboard component', async function(assert) {
    await render(hbs`{{rsa-dashboard}}`);
    assert.equal(document.querySelectorAll('.rsa-dashboard').length, 1);

  });

  test('it renders specific layout based on passed config', async function(assert) {
    this.set('dashbordConfig', {
      layout: 'two-column'
    });
    await render(hbs`
      {{#rsa-dashboard layoutStyle='two-column' config=dashbordConfig as |db|}}
        {{db.layout}}
      {{/rsa-dashboard}}
    `);
    assert.equal(document.querySelectorAll('.rsa-dashboard').length, 1);
    assert.equal(document.querySelectorAll('.rsa-dashboard .two-column-layout').length, 1);
  });
});

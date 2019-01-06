import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | widget/top-alerts', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the top alerts', async function(assert) {

    this.set('config', {
      data: [
        {
          name: 'test'
        },

        {
          name: 'test-2'
        }
      ]
    });
    await render(hbs`<div class="rsa-dashboard">
      {{#widget/top-alerts config=config as |ta|}}
        {{#ta.title}}{{/ta.title}}
        {{ta.content}}
      {{/widget/top-alerts}}</div>
    `);

    assert.equal(document.querySelectorAll('[test-id=widget-title]').length, 1, 'One title rendered');
    assert.equal(document.querySelectorAll('[test-id=top-item]').length, 2, 'Two items rendered');
  });
});

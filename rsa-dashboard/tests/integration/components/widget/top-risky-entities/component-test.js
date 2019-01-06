import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | widget/top-risky-entities', function(hooks) {
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
      {{#widget/top-risky-entities config=config as |ta|}}
        {{#ta.title}}{{/ta.title}}
        {{ta.content}}
      {{/widget/top-risky-entities}}</div>
    `);

    assert.equal(document.querySelectorAll('[test-id=widget-title]').length, 1, 'One title rendered');
    assert.equal(document.querySelectorAll('[test-id=top-risk-item]').length, 2, 'Two items rendered');
  });
});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | semi-circle', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('entity-details')
  });

  test('it renders semi-circle for given percentage', async function(assert) {
    await render(hbs`
      {{semi-circle percentage="10"}}
    `);
    return waitUntil(() => this.$('svg').length === 1).then(() => {
      assert.equal(this.element.textContent.trim(), '10%');
      assert.equal(this.$('.rsa-semi-circle_value')[0].outerHTML, '<circle class="rsa-semi-circle_value" cx="25" cy="25" r="20" stroke-width="5" style="stroke-dasharray: 125.664px; stroke-dashoffset: 113.097px;"></circle>');
    });
  });

  test('it renders different semi-circle for new percentage', async function(assert) {
    await render(hbs`
      {{semi-circle percentage="20"}}
    `);
    return waitUntil(() => this.$('svg').length === 1).then(() => {
      assert.equal(this.element.textContent.trim(), '20%');
      assert.equal(this.$('.rsa-semi-circle_value')[0].outerHTML, '<circle class="rsa-semi-circle_value" cx="25" cy="25" r="20" stroke-width="5" style="stroke-dasharray: 125.664px; stroke-dashoffset: 100.531px;"></circle>');
    });
  });
});
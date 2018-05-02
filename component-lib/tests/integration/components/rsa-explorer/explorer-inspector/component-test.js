import { module, test } from 'qunit';
import { render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | RSA Explorer Inspector', function(hooks) {
  setupRenderingTest(hooks);

  test('The explorer/explorer-inspector component renders to the DOM', async function(assert) {
    await render(hbs`{{rsa-explorer/explorer-inspector}}`);
    assert.equal(this.$('.rsa-explorer-inspector').length, 1, 'The explorer-inspector component should be found in the DOM');
    assert.equal(this.$('.back a').length, 0, 'There is NO back link');
  });

  test('it creates a back-to-route link', async function(assert) {
    await render(hbs`{{rsa-explorer/explorer-inspector backToRouteText='Back to before' backToRouteName='before' }}`);
    assert.equal(this.$('.rsa-explorer-inspector').length, 1, 'The explorer-inspector component should be found in the DOM');
    assert.equal(this.$('.back a').length, 1, 'There is a back link');
    assert.equal(this.$('.back a i[title="Back to before"]').length, 1, 'The back link has an icon with the backToRoute text as the title');
  });

});
import { module, test } from 'qunit';
import { render, findAll } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | RSA Explorer Inspector', function(hooks) {
  setupRenderingTest(hooks);

  test('The explorer/explorer-inspector component renders to the DOM', async function(assert) {
    await render(hbs`{{rsa-explorer/explorer-inspector}}`);
    assert.equal(findAll('.rsa-explorer-inspector').length, 1, 'The explorer-inspector component should be found in the DOM');
    assert.equal(findAll('.back a').length, 0, 'There is NO back link');
  });

  test('Setting the hasInspectorToolbar hides the selection', async function(assert) {
    await render(hbs`{{rsa-explorer/explorer-inspector hasInspectorToolbar=false}}`);
    assert.notOk(find('.inspector-toolbar'), 'Inspector toolbar is not showing');
  });

  test('it creates a back-to-route link', async function(assert) {
    await render(hbs`{{rsa-explorer/explorer-inspector backToRouteText='Back to before' backToRouteName='before' }}`);
    assert.equal(findAll('.rsa-explorer-inspector').length, 1, 'The explorer-inspector component should be found in the DOM');
    assert.equal(findAll('.back a').length, 1, 'There is a back link');
    assert.equal(findAll('.back a i[title="Back to before"]').length, 1, 'The back link has an icon with the backToRoute text as the title');
  });

});
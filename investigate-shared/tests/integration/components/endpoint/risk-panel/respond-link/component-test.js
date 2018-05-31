import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/risk-panel/respond-link', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    this.set('id', 'INC-1');
    await render(hbs`{{endpoint/risk-panel/respond-link entryId=id}}`);
    assert.equal(find('a.respond-link').textContent, 'INC-1', 'Link title is correct');
    assert.equal(find('a.respond-link').href.endsWith('/respond/incident/INC-1'), true, 'link url is correct');
  });
});

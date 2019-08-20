import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | list details', function(hooks) {
  setupRenderingTest(hooks);

  test('Nehal renders list details with correct components', async function(assert) {

    this.set('updateView', () => {});

    await render(hbs`{{list-manager/list-details updateView=updateView }}`);
    assert.ok(find('.list-details'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons.length, 2, 'Cancel and save buttons rendered');
  });
});

import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | list details - details footer', function(hooks) {
  setupRenderingTest(hooks);

  test('renders footer for list details with correct components', async function(assert) {
    assert.expect(5);
    this.set('updateView', () => {
      assert.ok(true, 'clicking button executes updateView');
    });

    await render(hbs`{{list-manager/list-details/details-footer updateView=updateView }}`);
    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Cancel');
    await click(buttons[0]);

    assert.equal(buttons[1].textContent.trim(), 'Save');
    await click(buttons[1]);

  });
});

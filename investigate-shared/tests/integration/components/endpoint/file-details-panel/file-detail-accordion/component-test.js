import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/file-details-panel/file-detail-accordion', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs`{{endpoint/file-details-panel/file-detail-accordion}}`);
    assert.equal(findAll('.file-details-panel__accordian__item').length, 2, '2 accordians is present');
  });
});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll } from '@ember/test-helpers';

module('Integration | Component | form-container/schedule-config/scan-options', function(hooks) {
  setupRenderingTest(hooks);

  test('should render cpu max fields', async function(assert) {
    await render(hbs`{{form-container/schedule-config/scan-options}}`);
    assert.equal(findAll('.noUi-horizontal').length, 2, 'expected to have 2 slider fields');
  });
});

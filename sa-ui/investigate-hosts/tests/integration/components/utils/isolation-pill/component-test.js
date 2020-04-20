import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | utils/isolation pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  test('Isolation pill loaded', async function(assert) {
    await render(hbs `{{utils/isolation-pill}}`);
    assert.equal(findAll('.isolation-pill').length, 1, 'Isolation pill loaded');
  });
});
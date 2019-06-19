import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | process-details/alerts-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  test('it renders', async function(assert) {
    await render(hbs`{{process-details/alerts-container}}`);
    assert.equal(findAll('.alerts-container').length, 1);
  });
});

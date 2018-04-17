import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

module('Integration | Component | process-analysis-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  test('process-analysis/container renders', async function(assert) {

    await render(hbs`{{process-analysis-container}}`);

    assert.equal(findAll('.process-list-box').length, 3, '3 columns present');
  });
});

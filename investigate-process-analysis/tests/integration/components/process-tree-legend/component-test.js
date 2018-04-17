import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

module('Integration | Component | process-tree-legend', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });


  test('it renders static legends', async function(assert) {
    await render(hbs`{{process-tree-legend}}`);
    assert.equal(findAll('.legend').length, 4, 'Expected to render 4 legends');
  });
});

import { findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

module('Integration | Component | Query Pills', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('Upon initialization, one active pill is created and tracked', async function(assert) {
    await render(hbs`{{query-container/query-pills}}`);
    assert.equal(findAll('.query-pill').length, 1, 'There should only be one query-pill.');
  });
});
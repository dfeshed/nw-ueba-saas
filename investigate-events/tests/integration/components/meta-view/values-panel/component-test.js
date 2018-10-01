import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | Values Panel', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it renders', async function(assert) {
    await render(hbs`{{meta-view/values-panel}}`);
    assert.ok(find('.rsa-investigate-meta-values-panel'));
  });
});
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render } from '@ember/test-helpers';

module('Integration | Component | host detail header', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  test('host header is rendered', async function(assert) {
    await render(hbs`{{host-detail/header}}`);
    assert.equal(findAll('.host-header').length, 1, 'Host header is visible');
    assert.equal(findAll('.host-header .titlebar').length, 1, 'Host titlebar is visible');
    assert.equal(findAll('.host-header .host-overview').length, 1, 'Host header overview is visible');
    assert.equal(findAll('.host-header .actionbar').length, 1, 'Host actionbar is visible');
  });
});

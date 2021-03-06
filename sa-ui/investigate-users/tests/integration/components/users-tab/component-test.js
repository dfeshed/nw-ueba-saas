import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | users-tab', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it renders', async function(assert) {
    await render(hbs`{{users-tab}}`);
    assert.equal(findAll('.users-tab').length, 1);
    assert.equal(findAll('.users-tab_filter').length, 1);
    assert.equal(findAll('.users-tab_body').length, 1);
  });
});

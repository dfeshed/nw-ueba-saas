import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | users-tab/body', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  test('it renders', async function(assert) {
    await render(hbs`{{users-tab/body}}`);
    assert.equal(findAll('.users-tab_body_header_bar').length, 1);
    assert.equal(findAll('.users-tab_body_header_bar_count').length, 1);
    assert.equal(findAll('.users-tab_body_header_bar_control').length, 1);
    assert.equal(findAll('.users-tab_body_list').length, 1);
    assert.equal(findAll('.users-tab_body_list_loader').length, 1);
  });
});

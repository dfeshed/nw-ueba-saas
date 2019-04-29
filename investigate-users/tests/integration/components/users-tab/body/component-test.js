import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;
module('Integration | Component | users-tab/body', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    new ReduxDataHelper(setState).build();
    await render(hbs`{{users-tab/body}}`);
    assert.equal(findAll('.users-tab_body_header_bar').length, 1);
    assert.equal(findAll('.users-tab_body_header_bar_count').length, 1);
    assert.equal(findAll('.users-tab_body_header_bar_control').length, 1);
    assert.equal(findAll('.users-tab_body_list').length, 1);
    assert.equal(findAll('.users-tab_body_list_loader').length, 1);
  });

  test('it should show loader till data is not there', async function(assert) {
    new ReduxDataHelper(setState).users([]).build();
    await render(hbs`{{users-tab/body}}`);
    assert.equal(findAll('.rsa-loader').length, 1);
  });

  test('it should throw error if there is some error', async function(assert) {
    new ReduxDataHelper(setState).users([]).usersError('Test').build();
    await render(hbs`{{users-tab/body}}`);
    assert.equal(findAll('.users-tab_body_list').length, 0);
    assert.equal(findAll('.rsa-loader').length, 0);
  });

});

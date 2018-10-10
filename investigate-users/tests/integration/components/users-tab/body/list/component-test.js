import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import users from '../../../../../data/presidio/user-list';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | users-tab/body/list', function(hooks) {
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
    await render(hbs`{{users-tab/body/list}}`);
    assert.equal(findAll('.rsa-data-table').length, 1);
  });

  test('it renders with proper user data', async function(assert) {
    new ReduxDataHelper(setState).users(users.data).totalUsers(120).build();
    await render(hbs`{{users-tab/body/list}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 2);
    assert.equal(find('.rsa-data-table-body-rows').textContent.replace(/\s/g, ''), '45auth_qa_1_23Alerts40qa_ad_contains_static2Alerts');
  });
});

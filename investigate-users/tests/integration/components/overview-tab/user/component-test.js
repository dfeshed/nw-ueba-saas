import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import usrOverview from '../../../../data/presidio/usr_overview';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | overview-tab/user', function(hooks) {
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
    await render(hbs `{{overview-tab/user}}`);
    assert.equal(find('.user-overview-tab_title').textContent.trim(), 'High Risk Users');
  });

  test('it should show proper count', async function(assert) {
    new ReduxDataHelper(setState).topUsers(usrOverview.data).build();
    await render(hbs `{{overview-tab/user}}`);
    assert.equal(findAll('.user-overview-tab_upper_users_row').length, 5);
    assert.equal(findAll('.rsa-icon-account-group-5-filled').length, 1);
  });
});
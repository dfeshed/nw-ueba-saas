import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | users-tab/body/header', function(hooks) {
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
    await render(hbs`{{users-tab/body/header}}`);
    assert.equal(findAll('.users-tab_body_header_bar').length, 1);
    assert.equal(findAll('.users-tab_body_header_bar_count').length, 1);
    assert.equal(findAll('.users-tab_body_header_bar_control').length, 1);
  });

  test('it renders with proper user count', async function(assert) {
    new ReduxDataHelper(setState).userSeverity([{
      Critical: { userCount: 10 },
      High: { userCount: 17 },
      Low: { userCount: 13 },
      Medium: { userCount: 1 }
    }]).totalUsers(120).build();
    await render(hbs`{{users-tab/body/header}}`);
    assert.equal(find('.severity-bar').textContent.replace(/\s/g, ''), '10Critical17High1Medium13Low');
    assert.equal(find('.users-tab_body_header_bar_count').textContent.replace(/\s/g, ''), '120UsersSortBy:RiskScore');
  });
});

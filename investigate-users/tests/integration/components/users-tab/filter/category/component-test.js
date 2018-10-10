import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { initialFilterState } from 'investigate-users/reducers/users/selectors';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | users-tab/filter/category', function(hooks) {
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
    await render(hbs`{{users-tab/filter/category}}`);
    assert.equal(find('.users-tab_filter_user').textContent.replace(/\s/g, ''), 'RiskyUsers(0)WatchlistUsers(0)AdminUsers(0)');
  });

  test('it renders with counts for risky admin and watched', async function(assert) {
    new ReduxDataHelper(setState).usersCount(10, 20, 30).build();
    await render(hbs`{{users-tab/filter/category}}`);
    assert.equal(find('.users-tab_filter_user').textContent.replace(/\s/g, ''), 'RiskyUsers(10)WatchlistUsers(20)AdminUsers(30)');
  });

  test('it renders with selected filter', async function(assert) {
    new ReduxDataHelper(setState).usersCount(10, 20, 30).usersFilter({ ...initialFilterState, isWatched: true, minScore: 0, userTags: ['admin'] }).build();
    await render(hbs`{{users-tab/filter/category}}`);
    assert.equal(findAll('.rsa-form-button-clicked').length, 3);
    await this.$("button:contains('Risky')").click();
    return settled();
  });

  test('it renders with selected filter for watchlist', async function(assert) {
    new ReduxDataHelper(setState).usersCount(10, 20, 30).usersFilter({ ...initialFilterState, isWatched: true, minScore: 0, userTags: ['admin'] }).build();
    await render(hbs`{{users-tab/filter/category}}`);
    assert.equal(findAll('.rsa-form-button-clicked').length, 3);
    await this.$("button:contains('Watchlist')").click();
    return settled();
  });

  test('it renders with selected filter for admin', async function(assert) {
    new ReduxDataHelper(setState).usersCount(10, 20, 30).usersFilter({ ...initialFilterState, isWatched: true, minScore: 0, userTags: ['admin'] }).build();
    await render(hbs`{{users-tab/filter/category}}`);
    assert.equal(findAll('.rsa-form-button-clicked').length, 3);
    await this.$("button:contains('Admin')").click();
    return settled();
  });
});

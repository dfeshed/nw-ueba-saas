import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { initialFilterState } from 'investigate-users/reducers/users/selectors';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchFetch } from '../../../../../helpers/patch-fetch';
import { Promise } from 'rsvp';

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
    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return {};
          }
        });
      });
    });
  });

  test('it renders', async function(assert) {
    await render(hbs`{{users-tab/filter/category}}`);
    assert.equal(findAll('.risky').length, 1);
    assert.equal(findAll('.watched').length, 1);
  });

  test('it renders with counts for risky admin and watched', async function(assert) {
    new ReduxDataHelper(setState).usersCount(10, 20).build();
    await render(hbs`{{users-tab/filter/category}}`);
    assert.ok(find('.risky').parentElement.innerText.indexOf('10') > 0);
    assert.ok(find('.watched').parentElement.innerText.indexOf('20') > 0);
  });

  test('it renders with selected filter', async function(assert) {
    new ReduxDataHelper(setState).usersCount(10, 20).usersFilter({ ...initialFilterState, isWatched: true, minScore: 0, userTags: ['risky'] }).build();
    await render(hbs`{{users-tab/filter/category}}`);
    assert.equal(findAll('.rsa-form-button-clicked').length, 2);
    await find('.users-tab_filter_user_row button').click();
    return settled();
  });

  test('it renders with selected filter for watchlist', async function(assert) {
    new ReduxDataHelper(setState).usersCount(10, 20).usersFilter({ ...initialFilterState, isWatched: true, minScore: 0, userTags: ['watched'] }).build();
    await render(hbs`{{users-tab/filter/category}}`);
    assert.equal(findAll('.rsa-form-button-clicked').length, 2);
    await find('.users-tab_filter_user_row:nth-child(2) button').click();
    return settled();
  });
});

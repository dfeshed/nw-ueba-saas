import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Service from '@ember/service';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import Immutable from 'seamless-immutable';

let setState;

const initialFilterState = Immutable.from({
  addAlertsAndDevices: true,
  addAllWatched: true,
  alertTypes: null,
  departments: null,
  indicatorTypes: null,
  isWatched: false,
  locations: null,
  minScore: null,
  severity: null,
  sortDirection: 'DESC',
  sortField: 'score',
  fromPage: 1,
  size: 25,
  userTags: null
});

const routerStub = Service.extend({
  transitionTo: (route) => {
    return route;
  }
});

module('Integration | Component | overview-tab/watched', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:-routing', routerStub);
  });

  test('it renders', async function(assert) {
    await render(hbs `{{overview-tab/watched}}`);
    assert.equal(find('.user-overview-tab_title').textContent.trim(), 'All Users');
  });

  test('it should show proper count and update filter for risky', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState).usersCount(10, 20, 30).build();
    const updatedFilter = initialFilterState.merge({ minScore: 0 });
    this.set('applyUserFilter', (filterToUpdate) => {
      assert.deepEqual(filterToUpdate, updatedFilter);
    });
    await render(hbs `{{overview-tab/watched applyUserFilter=applyUserFilter}}`);
    assert.equal(find('.user-overview-tab_lower_users_watched').textContent.replace(/\s/g, ''), '10RiskyUsers20Watched');
    await click('.user-overview-tab_lower_users_watched_risk');
  });

  test('it should show proper count and update filter for watched', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState).usersCount(10, 20, 30).build();
    const updatedFilter = initialFilterState.merge({ isWatched: true });
    this.set('applyUserFilter', (filterToUpdate) => {
      assert.deepEqual(filterToUpdate, updatedFilter);
    });
    await render(hbs `{{overview-tab/watched applyUserFilter=applyUserFilter}}`);
    assert.equal(find('.user-overview-tab_lower_users_watched').textContent.replace(/\s/g, ''), '10RiskyUsers20Watched');
    await click('.user-overview-tab_lower_users_watched_watched');
  });
});
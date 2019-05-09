import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import EventColumnGroups from '../../../../../data/subscriptions/investigate-columns/data';

let setState;

const eventResultsData = [
  { sessionId: 1, medium: 32, time: +(new Date()), size: 13191, custom: { 'meta-summary': 'bar' }, 'has.alias': 'raw-value' },
  { sessionId: 2, medium: 32, time: +(new Date()), size: 13191, custom: { 'meta-summary': 'bar' }, 'has.alias': 'raw-value' }
];

module('Integration | Component | search-controls', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('renders clear-search-trigger when searchTerm', async function(assert) {
    new ReduxDataHelper(setState).columnGroup('SUMMARY').eventsPreferencesConfig().visibleColumns().eventResults(eventResultsData).columnGroups(EventColumnGroups).searchTerm('Log').build();
    await render(hbs`{{events-table-container/header-container/search-controls}}`);
    assert.equal(findAll('div.search-controls').length, 1);
    assert.equal(findAll('.clear-search-trigger').length, 1);
  });

  test('does not render clear-search-trigger when no searchTerm', async function(assert) {
    new ReduxDataHelper(setState).searchTerm(null).build();
    await render(hbs`{{events-table-container/header-container/search-controls}}`);
    assert.equal(findAll('.clear-search-trigger').length, 0);
  });

  test('renders next and prev triggers when searchMatches', async function(assert) {
    new ReduxDataHelper(setState).columnGroup('SUMMARY').eventsPreferencesConfig().visibleColumns().eventResults(eventResultsData).columnGroups(EventColumnGroups).searchTerm('Log').build();
    await render(hbs`{{events-table-container/header-container/search-controls}}`);
    assert.equal(findAll('.next-search-trigger').length, 1);
    assert.equal(findAll('.prev-search-trigger').length, 1);
  });

  test('does not render next and prev triggers when no searchMatches', async function(assert) {
    new ReduxDataHelper(setState).build();
    await render(hbs`{{events-table-container/header-container/search-controls}}`);
    assert.equal(findAll('.next-search-trigger').length, 0);
    assert.equal(findAll('.prev-search-trigger').length, 0);
  });
});

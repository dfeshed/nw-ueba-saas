import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

// FILTER_TYPES snippet from 'admin-source-management/addon/components/usm-policies/policies/filters/filter-types.js'...
// There's no need to keep the files sync'd - we only need one initial filter for testing purposes.
const FILTER_TYPES = [
  {
    'name': 'publishStatus',
    'label': 'adminUsm.policies.list.publishStatus',
    'listOptions': [
      // policy.lastPublishedOn > 0 ???
      { name: 'published', label: 'adminUsm.publishStatus.published' },
      // policy.lastPublishedOn === 0
      { name: 'unpublished', label: 'adminUsm.publishStatus.unpublished' },
      // policy.dirty === true
      { name: 'unpublished_edits', label: 'adminUsm.publishStatus.unpublishedEdits' }
    ],
    type: 'list'
  }
];

// mirror 'admin-source-management/addon/reducers/usm/filters/filters-reducers.js'
// * maybe import it if it gets big or starts to change a lot
const filterState = Immutable.from({
  selectedFilter: null,
  expressionList: []
});

// let someFunction;

module('Integration | Component | usm-shared/filters-wrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    // someFunction = (someArg) => {
    //   console.log('do something...');
    // };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The wrapper & data-filters components appears in the DOM', async function(assert) {
    assert.expect(1);
    this.set('filterState', filterState);
    this.set('filterType', 'POLICIES');
    this.set('filterTypes', FILTER_TYPES);
    this.set('resetFilters', () => {});
    this.set('applyFilters', (/* reloadAction, expressions, belongsTo */) => {});
    this.set('applyFiltersReloadAction', () => {});
    await render(hbs`{{usm-shared/filters-wrapper
      filterState=filterState
      filterType=filterType
      filterTypes=filterTypes
      resetFilters=(action resetFilters)
      applyFilters=(action applyFilters (action applyFiltersReloadAction))}}`
    );
    assert.equal(findAll('.filter-wrapper .rsa-data-filters').length, 1, 'The component appears in the DOM');
  });

  test('applyFilters() called', async function(assert) {
    assert.expect(3);
    this.set('filterState', filterState);
    this.set('filterType', 'POLICIES');
    this.set('filterTypes', FILTER_TYPES);
    this.set('resetFilters', () => {});
    this.set('applyFilters', (reloadAction, expressions, belongsTo) => {
      reloadAction(); // reloadAction should be the applyFiltersReloadAction
      assert.equal(expressions.length, 1, 'applyFilters() called with 1 filter');
      assert.equal(belongsTo, 'POLICIES', 'applyFilters() called with expected filterType');
    });
    this.set('applyFiltersReloadAction', () => {
      assert.ok(true, 'applyFiltersReloadAction() called');
    });
    await render(hbs`{{usm-shared/filters-wrapper
      filterState=filterState
      filterType=filterType
      filterTypes=filterTypes
      resetFilters=(action resetFilters)
      applyFilters=(action applyFilters (action applyFiltersReloadAction))}}`
    );
    // published filter will be the first list-filter
    const [el] = findAll('.filter-controls .list-filter');
    // click the first filter option
    const [firstOpt] = el.querySelectorAll('.list-filter-option');
    await click(firstOpt);
  });

  test('resetFilters() called', async function(assert) {
    assert.expect(1);
    this.set('filterState', filterState);
    this.set('filterType', 'POLICIES');
    this.set('filterTypes', FILTER_TYPES);
    this.set('resetFilters', (belongsTo) => {
      assert.equal(belongsTo, 'POLICIES', 'resetFilters() called with expected filterType');
    });
    this.set('applyFilters', (/* action, filters, belongsTo */) => {});
    this.set('applyFiltersReloadAction', () => {});
    await render(hbs`{{usm-shared/filters-wrapper
      filterState=filterState
      filterType=filterType
      filterTypes=filterTypes
      resetFilters=(action resetFilters)
      applyFilters=(action applyFilters (action applyFiltersReloadAction))}}`
    );
    // click the Reset button
    const [resetBtn] = findAll('.filters-footer .reset-filter-button button');
    await click(resetBtn);
  });

});

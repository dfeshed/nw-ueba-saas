import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import EventColumnGroups from '../../../../../data/subscriptions/investigate-columns/data';
import { find, findAll, render, click } from '@ember/test-helpers';
import { typeInSearch } from 'ember-power-select/test-support/helpers';

const columnGroupManagerSelector = '.rsa-investigate-events-table__header__columnGroups';
const dropdownSelector = `${columnGroupManagerSelector} .rsa-split-dropdown button`;
const columnGroupItem = `${columnGroupManagerSelector} ul.rsa-item-list > li.rsa-list-item`;

let setState;

module('Integration | Component | Column Groups', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('columnGroup manager should be visible', async function(assert) {
    new ReduxDataHelper(setState).columnGroup('SUMMARY').columnGroups(EventColumnGroups).build();
    await render(hbs`{{events-table-container/header-container/column-groups}}`);

    assert.ok(find(columnGroupManagerSelector), 'Column Group Manager present');
    assert.equal(find(`${columnGroupManagerSelector} .list-caption`).textContent.trim(), 'Column Group: Summary List', 'Default Column group is Summary List.');
  });

  test('it provides option to select column groups', async function(assert) {

    assert.expect(7);

    new ReduxDataHelper(setState).columnGroup('SUMMARY').columnGroups(EventColumnGroups).build();
    await render(hbs`{{events-table-container/header-container/column-groups}}`);

    assert.ok(find(`${columnGroupManagerSelector} .rsa-button-menu.collapsed`), 'Column Group Menu collapsed');

    assert.ok(find(dropdownSelector), 'dropdown buttons present');
    await click(dropdownSelector);

    assert.ok(find(`${columnGroupManagerSelector} .rsa-button-menu.expanded`), 'Column Group Menu expanded');

    assert.ok(find(`${columnGroupManagerSelector} .rsa-item-list`), 'Column group list found');

    const options = findAll(columnGroupItem).map((d) => d.textContent.trim());
    assert.equal(options.join('').replace(/\s+/g, ''), 'Custom1Custom2SummaryListSummaryListSummaryListSummaryListEmailAnalysisMalwareAnalysisThreatAnalysisWebAnalysisEndpointAnalysis');

    const ootbIcon = 'rsa-icon-lock-close-1-lined';
    const nonOotbIcon = 'rsa-icon-settings-1-lined';

    assert.ok(findAll(`${columnGroupItem} i`)[0].classList.contains(nonOotbIcon), 'Custom1 is a custom column group');
    assert.ok(findAll(`${columnGroupItem} i`)[6].classList.contains(ootbIcon), 'Email Analysis is an OOTB column group');
  });

  test('persisted column group is preselected in the drop down and highlighted in the options', async function(assert) {

    new ReduxDataHelper(setState).columnGroup('MALWARE').columnGroups(EventColumnGroups).build();
    await render(hbs`{{events-table-container/header-container/column-groups}}`);

    assert.equal(find(`${columnGroupManagerSelector} .list-caption`).textContent.trim(), 'Column Group: Malware Analysis', 'Expected Malware Analysis to be selected');

    assert.ok(find(dropdownSelector), 'dropdown buttons present');
    await click(dropdownSelector);

    const selectedOptions = findAll(`${columnGroupItem}.is-selected`);
    assert.equal(selectedOptions.length, 1, '1 option selected');
    assert.equal(selectedOptions[0].textContent.trim(), 'Malware Analysis');

  });

  test('clicking entire row should close column group dropdown and update the selected column group', async function(assert) {

    new ReduxDataHelper(setState).columnGroup('EMAIL').columnGroups(EventColumnGroups).eventsPreferencesConfig().build();
    await render(hbs`{{events-table-container/header-container/column-groups}}`);

    assert.equal(find(`${columnGroupManagerSelector} .list-caption`).textContent.trim(), 'Column Group: Email Analysis');

    assert.ok(find(dropdownSelector), 'dropdown buttons present');
    await click(dropdownSelector);

    assert.ok(find(`${columnGroupManagerSelector} .rsa-button-menu.expanded`), 'Column Group Menu expanded');

    const optionsToSelect = findAll(`${columnGroupItem} a`);
    assert.equal(optionsToSelect.length, 11);

    // Prefer testing with top few options as they are already scrolled into view.
    await click(optionsToSelect[8]);

    assert.equal(find(`${columnGroupManagerSelector} .list-caption`).textContent.trim(), 'Column Group: Threat Analysis', 'Column group changed');

    assert.ok(find(`${columnGroupManagerSelector} .rsa-button-menu.collapsed`), 'Column Group Menu collapsed');
  });

  test('clicking away from the column group drop down should close the drop down', async function(assert) {

    new ReduxDataHelper(setState).columnGroup('EMAIL').columnGroups(EventColumnGroups).eventsPreferencesConfig().build();
    await render(hbs`<div class = 'other-div'></div>{{events-table-container/header-container/column-groups}}`);

    assert.ok(find(dropdownSelector), 'dropdown buttons present');
    await click(dropdownSelector);

    assert.ok(find(`${columnGroupManagerSelector} .rsa-button-menu.expanded`), 'Column Group Menu expanded');

    await click('.other-div');
    assert.ok(find(`${columnGroupManagerSelector} .rsa-button-menu.collapsed`), 'Column Group Menu expanded');
  });

  test('column groups can be filtered by name', async function(assert) {

    new ReduxDataHelper(setState).columnGroup('EMAIL').columnGroups(EventColumnGroups).eventsPreferencesConfig().build();
    await render(hbs`<div class = 'other-div'></div>{{events-table-container/header-container/column-groups}}`);

    await click(dropdownSelector);
    assert.ok(find(`${columnGroupManagerSelector} .list-filter`), 'filter component present');
    assert.ok(find(`${columnGroupManagerSelector} .list-filter .rsa-icon-filter-2-filled`), 'filter icon present');
    assert.equal(find(`${columnGroupManagerSelector} .list-filter input`).getAttribute('placeholder'), 'Filter column groups');
    assert.notOk(find('.clear-filter'), 'clear button not available');
    assert.equal(findAll(columnGroupItem).length, 11);

    await click(find(`${columnGroupManagerSelector} .list-filter input`));
    await typeInSearch('ma');
    assert.equal(findAll(columnGroupItem).length, 6);
    assert.ok(find('.clear-filter'), 'clear button now available');

    await click(find('.clear-filter button'));
    assert.equal(findAll(columnGroupItem).length, 11, 'filter is reset');

    await typeInSearch('mat');
    assert.equal(findAll(columnGroupItem).length, 0, 'all items filtered out');
    assert.equal(find('.no-results').textContent.trim(), 'All column groups have been excluded by the current filter');

    await click('.other-div');
    await click(dropdownSelector);
    assert.equal(findAll(columnGroupItem).length, 11, 'filter is reset when dropdown is closed and reopened');

  });

});

import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import waitForReduxStateChange from '../../../../helpers/redux-async-helpers';
import { initializePolicies, sortBy } from 'admin-source-management/actions/creators/policies-creators';

let setState, redux;

module('Integration | Component | usm-policies/policies', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    localStorage.clear();
    initialize(this.owner);
    redux = this.owner.lookup('service:redux');
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (sortField, isSortDescending) => {
      redux.dispatch(sortBy(sortField, isSortDescending));
      redux.dispatch(initializePolicies());
    };
  });

  test('The component appears in the DOM', async function(assert) {
    assert.expect(1);
    setState('name', false);
    const getItems = waitForReduxStateChange(redux, 'usm.policies.items');
    await render(hbs`{{usm-policies/policies}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-body').length, 1, 'The component appears in the DOM');
  });

  test('Policies filter panel and data-filters rendered', async function(assert) {
    assert.expect(2);
    setState('name', false);
    const getItems = waitForReduxStateChange(redux, 'usm.policies.items');
    await render(hbs`{{usm-policies/policies}}`);
    await getItems;
    assert.equal(findAll('.usm-policies-filter').length, 1, 'Policies filter panel rendered');
    assert.equal(findAll('.usm-policies-filter .rsa-data-filters').length, 1, 'data-filters rendered within policies filter panel');
  });

  test('Policy/Source Type filter rendered', async function(assert) {
    assert.expect(2);
    setState('name', false);
    const getItems = waitForReduxStateChange(redux, 'usm.policies.items');
    await render(hbs`{{usm-policies/policies}}`);
    await getItems;
    const translation = this.owner.lookup('service:i18n');
    const expectedFilterText = translation.t('adminUsm.policies.filter.sourceType');
    const exOptLen = 3;
    // policy/source type filter will be the 1st list-filter
    const [el] = findAll('.filter-controls .list-filter');
    assert.equal(el.querySelector('.filter-text').textContent, expectedFilterText, `rendered ${expectedFilterText} filter`);
    assert.equal(el.querySelectorAll('.list-filter-option').length, exOptLen, `There are ${exOptLen} filter options as expected`);
  });

  test('Publication Status filter rendered', async function(assert) {
    assert.expect(2);
    setState('name', false);
    const getItems = waitForReduxStateChange(redux, 'usm.policies.items');
    await render(hbs`{{usm-policies/policies}}`);
    await getItems;
    const translation = this.owner.lookup('service:i18n');
    const expectedFilterText = translation.t('adminUsm.policies.list.publishStatus');
    const exOptLen = 3;
    // published filter will be the 2nd list-filter
    const [, el] = findAll('.filter-controls .list-filter');
    assert.equal(el.querySelector('.filter-text').textContent, expectedFilterText, `rendered ${expectedFilterText} filter`);
    assert.equal(el.querySelectorAll('.list-filter-option').length, exOptLen, `There are ${exOptLen} filter options as expected`);
  });

  test('Show policy list', async function(assert) {
    assert.expect(4);
    setState('name', false);
    const getItems = waitForReduxStateChange(redux, 'usm.policies.items');
    await render(hbs`{{usm-policies/policies}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-header-cell').length, 6, 'Returned expected header rows of the datatable');
    assert.equal(findAll('.rsa-data-table-body-row').length, 11, 'Returned expected number of rows of the datatable');
    assert.equal(findAll('.rsa-data-table-body-row .missing-typespec').length, 11, 'All missing-typespec elements');
    assert.equal(findAll('.rsa-data-table-body-row .missing-typespec i').length, 1, 'One missing-typespec element with error');
  });

  test('Only Edr Default policy has the checkbox and not windows log and file default policies', async function(assert) {
    assert.expect(1);
    setState('name', false);
    const getItems = waitForReduxStateChange(redux, 'usm.policies.items');
    await render(hbs`{{usm-policies/policies}}`);
    await getItems;
    assert.equal(findAll('.usm-default-policy-row > .rsa-form-checkbox-wrapper').length, 1, 'EDR default policy has the checkbox');
  });

  test('Show policy list with sort=name ascending', async function(assert) {
    assert.expect(7);
    setState('name', false);
    const getItems = waitForReduxStateChange(redux, 'usm.policies.items');
    await render(hbs`{{usm-policies/policies}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-header-cell').length, 6, 'Returned expected header rows of the datatable');
    assert.equal(findAll('.rsa-data-table-body-row').length, 11, 'Returned expected number of rows of the datatable');

    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(1) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Default EDR Policy', 'row1 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(2) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Default File Policy', 'row2 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(4) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'EMC 001LongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAME', 'row4 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(7) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'EMC Reston! 014', 'row7 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(8) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'F001', 'row8 name value is as expected');
  });

  test('Show policy list with sort=name descending', async function(assert) {
    assert.expect(7);
    setState('name', true);
    const getItems = waitForReduxStateChange(redux, 'usm.policies.items');
    await render(hbs`{{usm-policies/policies}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-header-cell').length, 6, 'Returned expected header rows of the datatable');
    assert.equal(findAll('.rsa-data-table-body-row').length, 11, 'Returned expected number of rows of the datatable');

    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(1) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'WL002', 'row1 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(2) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'WL001', 'row2 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(5) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'EMC Reston! 014', 'row5 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(8) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'EMC 001LongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAMELongNAME',
      'row8 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(9) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Default Windows Log Policy', 'row9 name value is as expected');
  });

  test('Show policy list with sort=description ascending', async function(assert) {
    assert.expect(7);
    setState('description', false);
    const getItems = waitForReduxStateChange(redux, 'usm.policies.items');
    await render(hbs`{{usm-policies/policies}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-header-cell').length, 6, 'Returned expected header rows of the datatable');
    assert.equal(findAll('.rsa-data-table-body-row').length, 11, 'Returned expected number of rows of the datatable');

    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(1) .rsa-data-table-body-cell:nth-of-type(4)')[0].innerText.trim(),
      'Default EDR Policy __default_edr_policy', 'row1 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(3) .rsa-data-table-body-cell:nth-of-type(4)')[0].innerText.trim(),
      'EMC Bangalore 013 of policy policy_013', 'row3 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(4) .rsa-data-table-body-cell:nth-of-type(4)')[0].innerText.trim(),
      'EMC Reston 012 of policy policy_012', 'row4 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(8) .rsa-data-table-body-cell:nth-of-type(4)')[0].innerText.trim(),
      'These are the settings that are applied when not defined in another policy applied to an agent.', 'row8 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(9) .rsa-data-table-body-cell:nth-of-type(4)')[0].innerText.trim(),
      'These are the settings that are applied when not defined in another policy applied to an agent.', 'row9 description value is as expected');
  });

  test('Show applied to group with sort=name descending', async function(assert) {
    assert.expect(4);
    setState('name', true);
    const getItems = waitForReduxStateChange(redux, 'usm.policies.items');
    await render(hbs`{{usm-policies/policies}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(1) .rsa-data-table-body-cell:nth-of-type(3)')[0].innerText.trim(),
      'None', 'row1 applied to group value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(2) .rsa-data-table-body-cell:nth-of-type(3)')[0].innerText.trim(),
      'Group 01 , Group 02', 'row2 applied to group value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(8) .rsa-data-table-body-cell:nth-of-type(3)')[0].innerText.trim(),
      'Group 01 , Group 02', 'row8 applied to group value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(9) .rsa-data-table-body-cell:nth-of-type(3)')[0].innerText.trim(),
      'Base Policy', 'row9 applied to group value is as expected');
  });
});

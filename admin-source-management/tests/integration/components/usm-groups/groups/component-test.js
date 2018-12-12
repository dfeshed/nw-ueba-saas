import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import waitForReduxStateChange from '../../../../helpers/redux-async-helpers';
import { initializeGroups, sortBy } from 'admin-source-management/actions/creators/groups-creators';

let setState, redux;

module('Integration | Component | usm-groups/groups', function(hooks) {
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
      redux.dispatch(initializeGroups());
    };
  });

  test('The component appears in the DOM', async function(assert) {
    assert.expect(1);
    setState('name', false);
    const getItems = waitForReduxStateChange(redux, 'usm.groups.items');
    await render(hbs`{{usm-groups/groups}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-body').length, 1, 'The component appears in the DOM');
  });

  test('Show group list', async function(assert) {
    assert.expect(2);
    setState('name', false);
    const getItems = waitForReduxStateChange(redux, 'usm.groups.items');
    await render(hbs`{{usm-groups/groups}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-header-cell').length, 7, 'Returned expected header rows of the datatable');
    assert.equal(findAll('.rsa-data-table-body-row').length, 14, 'Returned expected number of rows of the datatable');
  });

  test('Show group list with sort=name ascending', async function(assert) {
    assert.expect(7);
    setState('name', false);
    const getItems = waitForReduxStateChange(redux, 'usm.groups.items');
    await render(hbs`{{usm-groups/groups}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-header-cell').length, 7, 'Returned expected header rows of the datatable');
    assert.equal(findAll('.rsa-data-table-body-row').length, 14, 'Returned expected number of rows of the datatable');

    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(1) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Awesome! 012', 'row1 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(2) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Basketball 011', 'row2 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(3) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Cat Woman 010', 'row3 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(10) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Volleyball 005', 'row10 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(12) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Xylaphone 003', 'row12 name value is as expected');
  });

  test('Show group list with sort=name descending', async function(assert) {
    assert.expect(7);
    setState('name', true);
    const getItems = waitForReduxStateChange(redux, 'usm.groups.items');
    await render(hbs`{{usm-groups/groups}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-header-cell').length, 7, 'Returned expected header rows of the datatable');
    assert.equal(findAll('.rsa-data-table-body-row').length, 14, 'Returned expected number of rows of the datatable');

    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(1) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Zebra 001', 'row1 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(2) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Yabba Dabba Doo! 002', 'row2 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(3) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Xylaphone 003', 'row3 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(10) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Excellent! 008', 'row10 name value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(12) .rsa-data-table-body-cell:nth-of-type(2)')[0].innerText.trim(),
      'Cat Woman 010', 'row12 name value is as expected');
  });

  test('Show group list with sort=description ascending', async function(assert) {
    assert.expect(7);
    setState('description', false);
    const getItems = waitForReduxStateChange(redux, 'usm.groups.items');
    await render(hbs`{{usm-groups/groups}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-header-cell').length, 7, 'Returned expected header rows of the datatable');
    assert.equal(findAll('.rsa-data-table-body-row').length, 14, 'Returned expected number of rows of the datatable');

    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(1) .rsa-data-table-body-cell:nth-of-type(5)')[0].innerText.trim(),
      'Awesome! 012 of group group_012', 'row1 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(2) .rsa-data-table-body-cell:nth-of-type(5)')[0].innerText.trim(),
      'Basketball 011 of group group_011', 'row2 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(3) .rsa-data-table-body-cell:nth-of-type(5)')[0].innerText.trim(),
      'Cat Woman 010 of group group_010', 'row3 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(10) .rsa-data-table-body-cell:nth-of-type(5)')[0].innerText.trim(),
      'Volleyball 005 of group group_005', 'row10 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(12) .rsa-data-table-body-cell:nth-of-type(5)')[0].innerText.trim(),
      'Xylaphone 003 of group group_003', 'row12 description value is as expected');
  });

  test('Shows correct source count', async function(assert) {
    assert.expect(10);
    const translation = this.owner.lookup('service:i18n');
    setState('name', true);
    const getItems = waitForReduxStateChange(redux, 'usm.groups.items');
    await render(hbs`{{usm-groups/groups}}`);
    await getItems;
    let expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountPublishedNewGroupTooltip');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(1) .rsa-data-table-body-cell:nth-of-type(3)')[0].innerText.trim(),
      'Updating',
      '-1 count is as expected');
    await triggerEvent('.rsa-data-table-body-row:nth-of-type(1) .rsa-data-table-body-cell:nth-of-type(3) .tooltip-text', 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[0].innerText.trim(),
      expectedSrcCount.string,
      '-1 count tooltip is as expected');

    expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountPublishedNoEndpointTooltip');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(3) .rsa-data-table-body-cell:nth-of-type(3)')[0].innerText.trim(),
      'N/A',
      '-2 count is as expected');
    await triggerEvent('.rsa-data-table-body-row:nth-of-type(3) .rsa-data-table-body-cell:nth-of-type(3) .tooltip-text', 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[1].innerText.trim(),
      expectedSrcCount.string,
      '-2 count tooltip is as expected');

    expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountUnpublishedNewGroupTooltip');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(13) .rsa-data-table-body-cell:nth-of-type(3)')[0].innerText.trim(),
      'N/A',
      '-3 count is as expected');
    await triggerEvent('.rsa-data-table-body-row:nth-of-type(13) .rsa-data-table-body-cell:nth-of-type(3) .tooltip-text', 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[2].innerText.trim(),
      expectedSrcCount.string,
      '-3 count tooltip is as expected');

    expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountUnpublishedEditedGroupTooltip');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(14) .rsa-data-table-body-cell:nth-of-type(3)')[0].innerText.trim(),
      30,
      'unpublished edit count is as expected');
    await triggerEvent('.rsa-data-table-body-row:nth-of-type(14) .rsa-data-table-body-cell:nth-of-type(3) .tooltip-text', 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[3].innerText.trim(),
      expectedSrcCount.string,
      'unpublished edit count tooltip is as expected');

    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(7) .rsa-data-table-body-cell:nth-of-type(3)')[0].innerText.trim(),
      10,
      'published and synced count is as expected');
    assert.ok(document.querySelectorAll('.tool-tip-value').length === 4, 'No tooltip expected for normal count');
  });

  test('Show group with source type and policies applied', async function(assert) {
    assert.expect(2);
    setState('name', true);
    const getItems = waitForReduxStateChange(redux, 'usm.groups.items');
    await render(hbs`{{usm-groups/groups}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(3) .rsa-data-table-body-cell:nth-of-type(6)')[0].innerText.trim(),
      'N/A', 'row3 source type value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(3) .rsa-data-table-body-cell:nth-of-type(4)')[0].innerText.trim(),
      'N/A', 'row3 policies applied value is as expected');
  });
});
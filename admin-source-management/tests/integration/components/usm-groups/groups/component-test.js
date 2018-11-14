import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
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

    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(1) .rsa-data-table-body-cell:nth-of-type(6)')[0].innerText.trim(),
      'Awesome! 012 of group group_012', 'row1 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(2) .rsa-data-table-body-cell:nth-of-type(6)')[0].innerText.trim(),
      'Basketball 011 of group group_011', 'row2 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(3) .rsa-data-table-body-cell:nth-of-type(6)')[0].innerText.trim(),
      'Cat Woman 010 of group group_010', 'row3 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(10) .rsa-data-table-body-cell:nth-of-type(6)')[0].innerText.trim(),
      'Volleyball 005 of group group_005', 'row10 description value is as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(12) .rsa-data-table-body-cell:nth-of-type(6)')[0].innerText.trim(),
      'Xylaphone 003 of group group_003', 'row12 description value is as expected');
  });

  skip('Shows correct source count', async function(assert) {
    assert.expect(4);
    const translation = this.owner.lookup('service:i18n');
    setState('name', true);
    const getItems = waitForReduxStateChange(redux, 'usm.groups.items');
    await render(hbs`{{usm-groups/groups}}`);
    await getItems;
    let expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountPublishedNewGroupTooltip');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(1) .rsa-data-table-body-cell:nth-of-type(7)')[0].innerText.trim(),
      expectedSrcCount.string,
      'first source count as expected');
    expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountPublishedNoEndpointTooltip');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(3) .rsa-data-table-body-cell:nth-of-type(7)')[0].innerText.trim(),
      expectedSrcCount.string,
      'second source count as expected');
    expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountUnpublishedGroupTooltip');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(2) .rsa-data-table-body-cell:nth-of-type(7)')[0].innerText.trim(),
      expectedSrcCount.string,
      'third source count as expected');
    assert.equal(findAll('.rsa-data-table-body-row:nth-of-type(4) .rsa-data-table-body-cell:nth-of-type(7)')[0].innerText.trim(),
      250,
      'fourth source count as expected');
  });
});

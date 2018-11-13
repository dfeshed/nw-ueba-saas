import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import waitForReduxStateChange from '../../../../helpers/redux-async-helpers';
import { initializeGroups } from 'admin-source-management/actions/creators/groups-creators';

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
    setState = () => {
      redux.dispatch(initializeGroups());
    };
  });

  test('The component appears in the DOM', async function(assert) {
    setState();
    const getItems = waitForReduxStateChange(redux, 'usm.groups.items');
    await render(hbs`{{usm-groups/groups}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-body').length, 1, 'The component appears in the DOM');
  });

  test('Show group list', async function(assert) {
    setState();
    const getItems = waitForReduxStateChange(redux, 'usm.groups.items');
    await render(hbs`{{usm-groups/groups}}`);
    await getItems;
    assert.equal(findAll('.rsa-data-table-header-cell').length, 7, 'Returned expected header rows of the datatable');
    assert.equal(findAll('.rsa-data-table-body-row').length, 14, 'Returned expected number of rows of the datatable');
  });

  test('Shows correct source count', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    setState();
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

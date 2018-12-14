import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { dllListData } from '../../../../../unit/state/state';
import processData from '../../../../../integration/components/state/process-data';
import Immutable from 'seamless-immutable';
import Service from '@ember/service';

const data = {
  parentPid: 1,
  owner: 'test',
  fileName: 'ntoskrnl.exe',
  pid: 'user1',
  path: 'C:\\Windows\\System32',
  signature: 'signed',
  launchArguments: 'xxx',
  creationTime: '12/12/2018'
};

const transitions = [];

let initState;
module('Integration | Component | endpoint host-detail/process/process-details', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'host',
      generateURL: () => {
        return;
      },
      transitionTo: (name, args, queryParams) => {
        transitions.push({ name, queryParams });
      }
    }));
  });

  hooks.afterEach(function() {
    revertPatch();
  });
  test('process-details component should not rendered if isProcessDetailsView is false', async function(assert) {
    new ReduxDataHelper(initState)
      .agentId(1)
      .dllList(dllListData)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .machineOSType('windows')
      .processDetails(data)
      .isProcessDetailsView(false)
      .build();

    await render(hbs`{{host-detail/process/process-details}}`);
    assert.equal(findAll('.host-process-details .rsa-page-layout').length, 0, 'process details page should not rendered');
  });

  test('process-details component rendered', async function(assert) {
    new ReduxDataHelper(initState)
      .agentId(1)
      .dllList(dllListData)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .machineOSType('windows')
      .processDetails(data)
      .isProcessDetailsView(true)
      .build();

    await render(hbs`{{host-detail/process/process-details}}`);
    assert.equal(findAll('.host-process-details').length, 1, 'process details page rendered');
    assert.equal(findAll('.host-process-details-action-bar').length, 1, 'process details action bar should rendered');
    assert.equal(findAll('.process-content-box').length, 1, 'summary panel should rendered');
    assert.equal(findAll('.process-content-box__accordion-content').length, 3, 'Accordions should render');
  });
  test('process-details accordions expand test', async function(assert) {
    new ReduxDataHelper(initState)
      .agentId(1)
      .dllList(dllListData)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .isProcessDetailsView(true)
      .machineOSType('windows')
      .processDetails(data)
      .build();

    await render(hbs`{{host-detail/process/process-details}}`);
    assert.equal(findAll('.process-content-box__accordion-content .is-collapsed').length, 3, 'On render all accordions should collapsed');
    await click(findAll('.process-content-box__accordion-content .is-collapsed')[0]);
    assert.equal(findAll('.process-dll-list').length, 1, 'On click only one process-dll-list table should show');
    await click(findAll('.process-content-box__accordion-content .is-collapsed')[0]);
    assert.equal(findAll('.process-image-hooks-list').length, 1, 'On click only one process-image-hooks-list table should show');
    await click(findAll('.process-content-box__accordion-content .is-collapsed')[1]);
    assert.equal(findAll('.process-suspicious-threads-list').length, 1, 'On click only one process-suspicious-threads-listtable should show');
    assert.equal(findAll('.process-content-box__accordion-content .is-collapsed').length, 2, 'On click only one accordion should expand');

  });
  test('Property panel on process-details', async function(assert) {
    new ReduxDataHelper(initState)
      .agentId(1)
      .dllList(dllListData)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .processDetails(data)
      .isProcessDetailsView(true)
      .selectedTab(null)
      .machineOSType('windows')
      .build();

    await render(hbs`{{host-detail/process/process-details}}`);
    await click(findAll('.process-content-box__accordion-content .is-collapsed')[0]);
    await click(findAll('.rsa-data-table-body-row')[0]);
    assert.equal(findAll('.host-property-panel').length, 1, 'process property panel rendered');
    await click(findAll('.close-zone')[1]);
    assert.equal(findAll('.show-right').length, 0, 'process property panel should close');

    const redux = this.owner.lookup('service:redux');
    const { endpoint: { process: { selectedDllRowIndex } } } = redux.getState();
    assert.equal(selectedDllRowIndex, -1, 'Process row index should reset');
  });

  test('Back to process page on process-details', async function(assert) {
    new ReduxDataHelper(initState)
      .agentId(1)
      .dllList(dllListData)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .processDetails(data)
      .selectedTab(null)
      .isProcessDetailsView(true)
      .machineOSType('windows')
      .build();

    await render(hbs`{{host-detail/process/process-details}}`);
    await click(findAll('.action-bar a')[0]);
    assert.deepEqual(transitions, [{
      name: 'hosts',
      queryParams: {
        pid: null,
        subTabName: null
      }
    }]);
  });
});

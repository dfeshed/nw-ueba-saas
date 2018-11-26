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
  });

  hooks.afterEach(function() {
    revertPatch();
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
      .build();

    await render(hbs`{{host-detail/process/process-details}}`);
    assert.equal(findAll('.host-process-details').length, 1, 'process details page rendered');
    assert.equal(findAll('.host-process-details-action-bar').length, 1, 'process details action bar should rendered');
    assert.equal(findAll('.process-content-box').length, 1, 'summary panel should rendered');
    assert.equal(findAll('.process-content-box__accordion-content').length, 3, 'Accordians should render');
  });
  test('Property panel on process-details', async function(assert) {
    new ReduxDataHelper(initState)
      .agentId(1)
      .dllList(dllListData)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .processDetails(data)
      .selectedTab(null)
      .machineOSType('windows')
      .build();

    await render(hbs`{{host-detail/process/process-details}}`);

    await click(findAll('.rsa-data-table-body-row')[0]);
    assert.equal(findAll('.host-property-panel').length, 1, 'process property panel rendered');
    await click(findAll('.close-zone')[1]);
    assert.equal(findAll('.show-right').length, 0, 'process property panel should close');
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
      .machineOSType('windows')
      .build();

    await render(hbs`{{host-detail/process/process-details}}`);
    await click(findAll('.back-to-process')[0]);
    const redux = this.owner.lookup('service:redux');
    const { endpoint: { visuals: { isProcessDetailsView } } } = redux.getState();
    assert.equal(isProcessDetailsView, false, 'process property panel should close');
  });
});

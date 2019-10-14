import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, findAll, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

const selectedData = [
  {
    id: '0E54BF10-5A88-4F81-89DC-9BA17794BBAE',
    machineIdentity: {
      machineName: 'RAR113-EPS',
      machineOsType: 'linux',
      agentMode: 'advanced'
    },
    version: '11.4.0.0',
    managed: true,
    serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
  },
  {
    id: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
    machineIdentity: {
      machineName: 'RemDbgDrv',
      machineOsType: 'windows',
      agentMode: 'advanced'
    },
    version: '11.4.0.0',
    managed: true,
    serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
  }
];

module('Integration | Component | host-table/action-bar/more-actions', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('Clicking more button will give Reset Risk score and Delete options', async function(assert) {
    new ReduxDataHelper(setState).scanCount(2).build();
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true);
    });
    this.set('isMFTEnabled', { isDisplayed: false });
    this.set('hostDetails', { isIsolated: false });
    await render(hbs`{{host-list/host-table/action-bar/more-actions showRiskScoreModal=showRiskScoreModal deleteAction=deleteAction isMFTEnabled=isMFTEnabled hostDetails=hostDetails}}`);
    assert.equal(document.querySelector('.host_more_actions button').textContent.trim(), 'More', 'action bar More button label');
    assert.equal(document.querySelectorAll('.host_more_actions .is-disabled').length, 0, 'action bar more button is enabled');
    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 2, '2 list options should render.');
    assert.equal(findAll('.rsa-dropdown-action-list li')[0].textContent.trim(), 'Reset Risk Score', 'Reset Risk Score option is rendered.');
    assert.equal(findAll('.rsa-dropdown-action-list li')[1].textContent.trim(), 'Delete', 'Delete option is rendered.');
  });

  test('Clicking more button, should not show options if hosts not selected', async function(assert) {
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true);
    });
    this.set('noHostsSelected', true);
    this.set('hostDetails', { isIsolated: false });
    await render(hbs`{{host-list/host-table/action-bar/more-actions showRiskScoreModal=showRiskScoreModal deleteAction=deleteAction noHostsSelected=noHostsSelected hostDetails=hostDetails}}`);
    assert.equal(document.querySelector('.host_more_actions button').textContent.trim(), 'More', 'action bar More button label');
    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 0, 'no list options should render.');
  });


  test('Clicking Delete options will call passed down action', async function(assert) {
    new ReduxDataHelper(setState).scanCount(2).build();
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true, 'passed action is called');
    });
    this.set('isMFTEnabled', { isDisplayed: false });
    this.set('hostDetails', { isIsolated: false });
    await render(hbs`{{host-list/host-table/action-bar/more-actions showRiskScoreModal=showRiskScoreModal deleteAction=deleteAction isMFTEnabled=isMFTEnabled hostDetails=hostDetails}}`);
    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 2, '2 list options should render.');
    assert.equal(findAll('.rsa-dropdown-action-list li')[0].textContent.trim(), 'Reset Risk Score', 'Reset Risk Score option is rendered.');
    assert.equal(findAll('.rsa-dropdown-action-list li')[1].textContent.trim(), 'Delete', 'Delete option is rendered.');
    await click(findAll('.rsa-dropdown-action-list li')[1]);
  });

  test('Clicking download mft options will call passed down action', async function(assert) {
    const selectedData = {
      id: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
      machineIdentity: {
        machineName: 'RemDbgDrv',
        machineOsType: 'windows',
        agentMode: 'advanced'
      },
      version: '11.4.0.0',
      managed: true,
      serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
    };
    new ReduxDataHelper(setState).scanCount(selectedData).build();
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true, 'passed action is called');
    });
    this.set('isMFTEnabled', { isDisplayed: true });
    this.set('requestMFTDownload', () => {
      assert.ok(true);
    });
    this.set('requestSystemDumpDownload', () => {
      assert.ok(true);
    });
    this.set('selectedHostList', [selectedData]);
    this.set('hostDetails', { isIsolated: false });

    await render(hbs`{{host-list/host-table/action-bar/more-actions
      showRiskScoreModal=showRiskScoreModal
      deleteAction=deleteAction
      isMFTEnabled=isMFTEnabled
      selectedHostList=selectedHostList
      requestMFTDownload=requestMFTDownload
      requestSystemDumpDownload=requestSystemDumpDownload
      hostDetails=hostDetails}}`);

    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 5, '5 list options should render as MFT is enabled.');
    assert.equal(findAll('.rsa-dropdown-action-list li')[3].textContent.trim(), 'Download MFT to Server', 'Download MFT option is rendered.');
    await click(findAll('.rsa-dropdown-action-list li')[3]);
  });

  test('Download mft option disabled when agent is migrated and not broker', async function(assert) {
    const selectedData = {
      id: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
      machineIdentity: {
        machineName: 'RemDbgDrv',
        machineOsType: 'windows',
        agentMode: 'advanced'
      },
      version: '11.4.0.0',
      managed: true,
      serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
    };
    new ReduxDataHelper(setState).scanCount(selectedData).build();
    this.set('requestSystemDumpDownload', () => {
      assert.ok(true);
    });
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true, 'passed action is called');
    });
    this.set('isMFTEnabled', { isDisplayed: true });
    this.set('requestMFTDownload', () => {
      assert.ok(true);
    });
    this.set('selectedHostList', [selectedData]);
    this.set('isAgentMigrated', true);
    this.set('hostDetails', { isIsolated: false });


    await render(hbs`{{host-list/host-table/action-bar/more-actions
      showRiskScoreModal=showRiskScoreModal
      deleteAction=deleteAction
      isMFTEnabled=isMFTEnabled
      selectedHostList=selectedHostList
      isAgentMigrated=isAgentMigrated
      requestMFTDownload=requestMFTDownload
      requestSystemDumpDownload=requestSystemDumpDownload
      hostDetails=hostDetails}}`);

    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li.downloadMFT-button .is-disabled').length, 1, 'Download MFT option is disabled.');
  });

  test('Download mft option enabled when agent is not migrated or is broker', async function(assert) {
    const selectedData = {
      id: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
      machineIdentity: {
        machineName: 'RemDbgDrv',
        machineOsType: 'windows',
        agentMode: 'advanced'
      },
      version: '11.4.0.0',
      managed: true,
      serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
    };
    new ReduxDataHelper(setState).scanCount(selectedData).build();
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('requestSystemDumpDownload', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true, 'passed action is called');
    });
    this.set('isMFTEnabled', { isDisplayed: true });
    this.set('requestMFTDownload', () => {
      assert.ok(true);
    });
    this.set('selectedHostList', [selectedData]);
    this.set('isAgentMigrated', false);
    this.set('hostDetails', { isIsolated: false });


    await render(hbs`{{host-list/host-table/action-bar/more-actions
      showRiskScoreModal=showRiskScoreModal
      deleteAction=deleteAction
      isMFTEnabled=isMFTEnabled
      selectedHostList=selectedHostList
      isAgentMigrated=isAgentMigrated
      requestMFTDownload=requestMFTDownload
      requestSystemDumpDownload=requestSystemDumpDownload
      hostDetails=hostDetails}}`);

    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li.downloadMFT-button .is-disabled').length, 0, 'Download MFT option is enabled.');
  });

  test('download mft options not present when more than 1 item is selected', async function(assert) {
    new ReduxDataHelper(setState).scanCount(...selectedData).build();
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true, 'passed action is called');
    });
    this.set('isMFTEnabled', { isDisplayed: true });
    this.set('hostDetails', { isIsolated: false });
    this.set('requestMFTDownload', () => {
      assert.ok(true);
    });
    this.set('selectedHostList', [...selectedData]);
    await render(hbs`{{host-list/host-table/action-bar/more-actions
      showRiskScoreModal=showRiskScoreModal
      deleteAction=deleteAction
      isMFTEnabled=isMFTEnabled
      selectedHostList=selectedHostList
      requestMFTDownload=requestMFTDownload
      hostDetails=hostDetails}}`);

    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 2, '2 list options should render as download MFT option should not render if more thatn one host is selected.');
  });

  test('More menu is not visible on clicking the disable button', async function(assert) {
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true);
    });
    this.set('hostDetails', { isIsolated: false });
    this.set('noHostsSelected', true);
    await render(hbs`{{host-list/host-table/action-bar/more-actions showRiskScoreModal=showRiskScoreModal deleteAction=deleteAction noHostsSelected=noHostsSelected}}`);
    assert.equal(document.querySelector('.host_more_actions button').textContent.trim(), 'More', 'action bar More button label');
    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 0, 'no list options should render.');
  });

  test('More menu contains the option to download system dump', async function(assert) {
    this.set('selectedHostList', [selectedData]);
    this.set('hostDetails', { isIsolated: false });
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true);
    });
    this.set('requestSystemDumpDownload', () => {
      assert.ok(true);
    });
    this.set('isMFTEnabled', { isDisplayed: true });
    this.set('noHostsSelected', false);
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', ['endpoint-server.agent.manage']);

    await render(hbs`{{host-list/host-table/action-bar/more-actions 
                      showRiskScoreModal=showRiskScoreModal 
                      isMFTEnabled=isMFTEnabled 
                      deleteAction=deleteAction 
                      selectedHostList=selectedHostList
                      noHostsSelected=noHostsSelected
                      requestSystemDumpDownload=requestSystemDumpDownload
                      hostDetails=hostDetails}}`);
    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 5, '4 options should render.');
    assert.equal(findAll('.download-system-dump-button').length, 1, 'Download System dump option is rendered.');
  });

  test('Hide the download memory dump button, when there is no right permission', async function(assert) {
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true);
    });
    this.set('requestMFTDownload', () => {
      assert.ok(true);
    });
    this.set('requestSystemDumpDownload', () => {
      assert.ok(true);
    });
    this.set('selectedHostList', [selectedData]);
    this.set('hostDetails', { isIsolated: false });
    this.set('isMFTEnabled', { isDisplayed: false });
    this.set('noHostsSelected', false);
    await render(hbs`{{host-list/host-table/action-bar/more-actions 
                      showRiskScoreModal=showRiskScoreModal 
                      isMFTEnabled=isMFTEnabled 
                      deleteAction=deleteAction 
                      selectedHostList=selectedHostList
                      noHostsSelected=noHostsSelected
                      requestMFTDownload=requestMFTDownload
                      requestSystemDumpDownload=requestSystemDumpDownload
                      hostDetails=hostDetails}}`);
    await click('.host_more_actions button');
    assert.equal(findAll('.download-system-dump-button').length, 0, 'Download System dump option is hidden.');
  });

  test('Hide the download memory dump option, for multiple files selected', async function(assert) {
    this.set('selectedHostList', selectedData);
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true);
    });
    this.set('isMFTEnabled', { isDisplayed: true });
    this.set('noHostsSelected', false);
    this.set('hostDetails', { isIsolated: false });
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', ['endpoint-server.agent.manage']);

    await render(hbs`{{host-list/host-table/action-bar/more-actions 
                      showRiskScoreModal=showRiskScoreModal 
                      isMFTEnabled=isMFTEnabled 
                      deleteAction=deleteAction 
                      selectedHostList=selectedHostList
                      noHostsSelected=noHostsSelected
                      hostDetails=hostDetails}}`);
    await click('.host_more_actions button');
    assert.equal(findAll('.download-system-dump-button').length, 0, 'Download System dump option is hidden.');
  });

  test('Hide the download memory dump option, when isMFTEnabled is false', async function(assert) {
    this.set('selectedHostList', selectedData);
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true);
    });
    this.set('isMFTEnabled', { isDisplayed: false });
    this.set('noHostsSelected', false);
    this.set('hostDetails', { isIsolated: false });
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', ['endpoint-server.agent.manage']);

    await render(hbs`{{host-list/host-table/action-bar/more-actions 
                      showRiskScoreModal=showRiskScoreModal 
                      isMFTEnabled=isMFTEnabled 
                      deleteAction=deleteAction 
                      selectedHostList=selectedHostList
                      noHostsSelected=noHostsSelected
                      hostDetails=hostDetails}}`);
    await click('.host_more_actions button');
    assert.equal(findAll('.download-system-dump-button').length, 0, 'Download System dump option is hidden.');
  });

  test('Request for download system dump is sent, on selecting Download System Dump option', async function(assert) {
    assert.expect(1);
    this.set('selectedHostList', [selectedData]);
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true);
    });
    this.set('requestSystemDumpDownload', () => {
      assert.ok(true);
    });
    this.set('requestMFTDownload', () => {
      assert.ok(true);
    });
    this.set('isMFTEnabled', { isDisplayed: true });
    this.set('noHostsSelected', false);
    this.set('isAgentMigrated', false);
    this.set('hostDetails', { isIsolated: false });
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', ['endpoint-server.agent.manage']);

    await render(hbs`{{host-list/host-table/action-bar/more-actions 
                      showRiskScoreModal=showRiskScoreModal 
                      isMFTEnabled=isMFTEnabled 
                      deleteAction=deleteAction 
                      selectedHostList=selectedHostList
                      noHostsSelected=noHostsSelected
                      isAgentMigrated=isAgentMigrated
                      requestMFTDownload=requestMFTDownload
                      requestSystemDumpDownload=requestSystemDumpDownload
                      hostDetails=hostDetails}}`);
    await click('.host_more_actions button');
    await click('.download-system-dump-button button');
  });

  test('Download system dump option disabled when agent is migrated and not broker', async function(assert) {
    const selectedData = {
      id: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
      machineIdentity: {
        machineName: 'RemDbgDrv',
        machineOsType: 'windows',
        agentMode: 'advanced'
      },
      version: '11.4.0.0',
      managed: true,
      serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
    };
    new ReduxDataHelper(setState).scanCount(selectedData).build();
    this.set('requestSystemDumpDownload', () => {
      assert.ok(true);
    });
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true, 'passed action is called');
    });
    this.set('isMFTEnabled', { isDisplayed: true });
    this.set('requestMFTDownload', () => {
      assert.ok(true);
    });
    this.set('selectedHostList', [selectedData]);
    this.set('isAgentMigrated', true);
    this.set('hostDetails', { isIsolated: false });


    await render(hbs`{{host-list/host-table/action-bar/more-actions
      showRiskScoreModal=showRiskScoreModal
      deleteAction=deleteAction
      isMFTEnabled=isMFTEnabled
      selectedHostList=selectedHostList
      isAgentMigrated=isAgentMigrated
      requestMFTDownload=requestMFTDownload
      requestSystemDumpDownload=requestSystemDumpDownload
      hostDetails=hostDetails}}`);

    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li.download-system-dump-button .is-disabled').length, 1, 'Download MFT option is disabled.');
  });
  test('Network isolation options rendered', async function(assert) {
    const selectedData = {
      id: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
      machineIdentity: {
        machineName: 'RemDbgDrv',
        machineOsType: 'windows',
        agentMode: 'advanced'
      },
      version: '11.4.0.0',
      managed: true,
      serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
    };
    new ReduxDataHelper(setState).scanCount(selectedData).build();
    this.set('requestSystemDumpDownload', () => {
      assert.ok(true);
    });
    this.set('showRiskScoreModal', () => {
      assert.ok(true);
    });
    this.set('deleteAction', () => {
      assert.ok(true, 'passed action is called');
    });
    this.set('isMFTEnabled', { isDisplayed: true });
    this.set('requestMFTDownload', () => {
      assert.ok(true);
    });
    this.set('selectedHostList', [selectedData]);
    this.set('isAgentMigrated', true);
    this.set('hostDetails', { agentId: '', isIsolated: false });


    await render(hbs`{{host-list/host-table/action-bar/more-actions
      showRiskScoreModal=showRiskScoreModal
      deleteAction=deleteAction
      isMFTEnabled=isMFTEnabled
      selectedHostList=selectedHostList
      isAgentMigrated=isAgentMigrated
      requestMFTDownload=requestMFTDownload
      requestSystemDumpDownload=requestSystemDumpDownload
      hostDetails=hostDetails}}`);

    await click('.host_more_actions button');
    assert.equal(findAll('.rsa-dropdown-action-list li.isolate-button').length, 1, 'host-network-isolation option is rendered.');
    await triggerEvent(findAll('.rsa-dropdown-action-list li.isolate-button button')[0], 'mouseover');
  });
});



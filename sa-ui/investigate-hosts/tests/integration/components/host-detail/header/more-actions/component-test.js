import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { click, find, findAll, render, settled, triggerEvent } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { waitForSockets } from '../../../../../helpers/wait-for-sockets';

let setState;
const data = {
  serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0',
  agentStatus: {
    agentId: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
    lastSeenTime: '2019-05-09T09:22:09.713+0000',
    scanStatus: 'completed',
    isolationStatus: {
      isolated: false
    }
  },
  machineIdentity: {
    id: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
    machineName: 'RemDbgDrv',
    agentMode: 'advanced',
    agentVersion: '11.4.0.0',
    machineOsType: 'windows'
  },
  groupPolicy: {
    managed: true
  }
};
const policy = {
  policy: {
    edrPolicy: {
      name: 'Default EDR Policy',
      isolationConfig: {
        enabled: true
      }
    }
  }
};

module('Integration | Component | host-detail/header/more-actions', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
    window.localStorage.removeItem('doNotShowDownloadModal');
  });

  test('test for More Actions', async function(assert) {
    new ReduxDataHelper(setState)
      .hostOverview(data)
      .policy(policy)
      .isJsonExportCompleted(true)
      .build();
    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    assert.equal(findAll('.host-details_dropdown-action-list li').length, 5, 'Number of actions present is 5 as MFT and system dump permission added');
    assert.equal(findAll('.host-details_dropdown-action-list li.disabled').length, 2, 'export host detail is disabled');

    assert.ok(find('.host-start-scan-button'), 'scan-command renders giving the start scan button');
    assert.equal(findAll('.rsa-icon-check-shield').length, 0, 'Start scan icon not present');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(2)').textContent.trim(), 'Export Host details', 'Export Host details button renders');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(3)').textContent.trim(), 'Network Isolation', 'Network Isolation button renders');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(4)').textContent.trim(), 'Download MFT to Server', 'Download MFT button renders');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(5)').textContent.trim(), 'Download System Dump to Server', 'Download system dump button renders');
    assert.equal(findAll('.host-details_dropdown-action-list li.manual-file-download-button').length, 0, 'Manual file download  button renders');
  });

  test('test for More Actions when version is 11.5 and above', async function(assert) {
    new ReduxDataHelper(setState)
      .hostOverview(data)
      .machineIdentityAgentVersion('11.5.0.0')
      .policy(policy)
      .isJsonExportCompleted(true)
      .build();
    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    assert.equal(findAll('.host-details_dropdown-action-list li').length, 6, 'Number of actions present is 5 as MFT and system dump permission added');
    assert.equal(findAll('.host-details_dropdown-action-list li.disabled').length, 2, 'export host detail is disabled');

    assert.ok(find('.host-start-scan-button'), 'scan-command renders giving the start scan button');
    assert.equal(findAll('.rsa-icon-check-shield').length, 0, 'Start scan icon not present');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(2)').textContent.trim(), 'Export Host details', 'Export Host details button renders');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(3)').textContent.trim(), 'Network Isolation', 'Network Isolation button renders');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(4)').textContent.trim(), 'Download MFT to Server', 'Download MFT button renders');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(5)').textContent.trim(), 'Download System Dump to Server', 'Download system dump button renders');
    assert.equal(findAll('.host-details_dropdown-action-list li.manual-file-download-button').length, 1, 'Manual file download  button renders');
  });
  test('test for More Actions with no manage permission', async function(assert) {
    new ReduxDataHelper(setState)
      .host(data)
      .isJsonExportCompleted(true)
      .build();
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', []);
    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    assert.equal(findAll('.host-details_dropdown-action-list li').length, 2, 'Number of actions present is 2 as No MFT permission added');
    assert.equal(findAll('.host-details_dropdown-action-list li.disabled').length, 2, 'export host detail is disabled');
  });

  test('test for download warning modal rendering and for Requesting MFT', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();
    await render(hbs `
    <div id='modalDestination'></div>
    {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    patchSocket((method, model, query) => {
      assert.equal(method, 'downloadMFT');
      assert.deepEqual(query,
        {
          'data': {
            'agentId': 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
            'serverId': 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
          }
        });
    });
    await click('.host-details_dropdown-action-list li.downloadMFT-button button');
    assert.equal(findAll('.download-warning-modal').length, 2, 'modal window is rendered.');
    await click(findAll('.download-warning-modal .modal-content .close-modal.continue')[0]);
  });


  test('test for download warning modal not being visible and download continues', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();
    window.localStorage.setItem('doNotShowDownloadModal', 'true');
    await render(hbs `
    <div id='modalDestination'></div>
    {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    patchSocket((method, model, query) => {
      assert.equal(method, 'downloadMFT');
      assert.deepEqual(query,
        {
          'data': {
            'agentId': 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
            'serverId': 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
          }
        });
    });
    await click('.host-details_dropdown-action-list li.downloadMFT-button button');
    assert.equal(findAll('.download-warning-modal').length, 0, 'modal window does not show up, if doNotShowDownloadModal is set.');
    window.localStorage.removeItem('doNotShowDownloadModal');
  });


  test('test for download warning modal closes when cancelled', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();
    await render(hbs `
    <div id='modalDestination'></div>
    {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    await click('.host-details_dropdown-action-list li.downloadMFT-button button');
    await click(findAll('.download-warning-modal .modal-content .close-modal.cancel')[0]);
    assert.equal(findAll('.download-warning-modal').length, 0, 'download continues without showing the modal window.');
  });

  test('test for doNotShowModal persisting', async function(assert) {
    assert.expect(2);
    const done = waitForSockets();
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();
    await render(hbs `
    <div id='modalDestination'></div>
    {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    await click('.host-details_dropdown-action-list li.downloadMFT-button button');
    await click(findAll('.download-warning-modal .modal-content .download-warning-doNotShow .rsa-form-checkbox-label ')[0]);
    await click(findAll('.download-warning-modal .modal-content .close-modal.continue')[0]);
    assert.equal(findAll('.download-warning-modal').length, 0, 'download is cancelled and modal closes.');
    assert.equal(window.localStorage.getItem('doNotShowDownloadModal'), 'true', 'doNotShowModal persisted in the local storage when checkbox in modal was clicked and continued.');
    done();
  });

  test('test for, request system dump download', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();
    await render(hbs `
    <div id='modalDestination'></div>
    {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    patchSocket((method, model, query) => {
      assert.equal(method, 'downloadSystemDump');
      assert.deepEqual(query, {
        'data': 'A0351965-30D0-2201-F29B-FDD7FD32EB21'
      });
    });
    await click('.host-details_dropdown-action-list li.download-sys-dump-button button');
    assert.equal(findAll('.download-warning-modal').length, 2, 'modal window is rendered.');
    await click(findAll('.download-warning-modal .modal-content .close-modal.continue')[0]);
  });

  test('Test for download system dump button disabled', async function(assert) {
    const state = {
      serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0',
      agentStatus: {
        agentId: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
        lastSeenTime: '2019-05-09T09:22:09.713+0000',
        scanStatus: 'completed'
      },
      machineIdentity: {
        id: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
        machineName: 'RemDbgDrv',
        agentMode: 'advanced',
        agentVersion: '11.4.0.0',
        machineOsType: 'windows'
      },
      groupPolicy: {
        managed: false
      }
    };

    new ReduxDataHelper(setState)
      .hostOverview(state)
      .build();

    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    assert.equal(findAll('.host-details_dropdown-action-list li.download-sys-dump-button .is-disabled').length, 1, 'downloadMFT-button disabled');
  });

  test('Test for download system dump button enabled', async function(assert) {
    new ReduxDataHelper(setState)
      .host(data)
      .build();

    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    assert.equal(findAll('.host-details_dropdown-action-list li.download-sys-dump-button .is-disabled').length, 0, 'downloadMFT-button enabled');
  });


  test('Test for download MFT button disabled', async function(assert) {
    const state = {
      serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0',
      agentStatus: {
        agentId: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
        lastSeenTime: '2019-05-09T09:22:09.713+0000',
        scanStatus: 'completed'
      },
      machineIdentity: {
        id: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
        machineName: 'RemDbgDrv',
        agentMode: 'advanced',
        agentVersion: '11.4.0.0',
        machineOsType: 'windows'
      },
      groupPolicy: {
        managed: false
      }
    };

    new ReduxDataHelper(setState)
      .hostOverview(state)
      .build();

    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    assert.equal(findAll('.host-details_dropdown-action-list li.downloadMFT-button .is-disabled').length, 1, 'downloadMFT-button disabled');
  });

  test('Test for download MFT button enabled', async function(assert) {
    new ReduxDataHelper(setState)
      .host(data)
      .build();

    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    assert.equal(findAll('.host-details_dropdown-action-list li.downloadMFT-button .is-disabled').length, 0, 'downloadMFT-button enabled');
  });

  test('test for Export to JSON', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .host(data)
      .isJsonExportCompleted(true)
      .scanTime('2017-01-01T10:23:49.452Z')
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .isSnapshotsAvailable(true)
      .build();

    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    patchSocket((method, model, query) => {
      assert.equal(method, 'exportFileContext');
      assert.deepEqual(query,
        {
          'data': {
            'agentId': 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
            'categories': [
              'AUTORUNS'
            ],
            'scanTime': '2017-01-01T10:23:49.452Z'
          }
        });
    });

    await click('.host-details_dropdown-action-list li:nth-child(2) button');
  });

  test('test for Export to JSON disabled', async function(assert) {
    new ReduxDataHelper(setState)
      .host(data)
      .isJsonExportCompleted(true)
      .scanTime('2017-01-01T10:23:49.452Z')
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .snapShot([])
      .build();

    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    assert.ok(find('.host-details_dropdown-action-list li:nth-child(2) .is-disabled'), 'Export Host details disabled when no snapshots available');
  });

  test('test when Export to JSON is in download status', async function(assert) {
    new ReduxDataHelper(setState)
      .host(data)
      .scanTime('2017-01-01T10:23:49.452Z')
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .isJsonExportCompleted(false)
      .isSnapshotsAvailable(true)
      .build();

    await render(hbs `{{host-detail/header/more-actions}}`);

    await click('.host_more_actions .host-details-more-actions');
    assert.ok(find('.host-details_dropdown-action-list li:nth-child(2) .is-disabled'), 'Export Host details disabled when in downloading state');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(2) .rsa-form-button-wrapper').textContent.trim(), 'Downloading', 'Export Host details is in downloading state and button is disabled');
  });

  test('when JSON export is completed', async function(assert) {
    new ReduxDataHelper(setState)
      .host(data)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .build();
    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    assert.equal(find('.host-details_dropdown-action-list li:nth-child(2) .rsa-form-button-wrapper').textContent.trim(), 'Export Host details', 'In initial state and when previous export is completed, button is active');
  });

  test('Test for absence of network isolation options when policy is disabled', async function(assert) {
    const isolationDisabled = {
      policy: {
        edrPolicy: {
          name: 'Default EDR Policy',
          isolationConfig:
          {
            enabled: false
          }
        }
      }
    };

    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .machineIdentityAgentVersion('11.4.0.0')
      .policy(isolationDisabled)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();
    await render(hbs `
      <div id='modalDestination'></div>
      {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    assert.equal(findAll('.host-details_dropdown-action-list li').length, 4, 'No Network isolation related options rendered');
  });

  test('Test for absence of network isolation options when policy is disabled when version is 11.5 and above', async function(assert) {
    const isolationDisabled = {
      policy: {
        edrPolicy: {
          name: 'Default EDR Policy',
          isolationConfig:
            {
              enabled: false
            }
        }
      }
    };

    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .machineIdentityAgentVersion('11.5.0.0')
      .policy(isolationDisabled)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();
    await render(hbs `
      <div id='modalDestination'></div>
      {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    assert.equal(findAll('.host-details_dropdown-action-list li').length, 5, 'No Network isolation related options rendered');
  });

  test('test for absence of network isolation options when policy is not present', async function(assert) {
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .machineIdentityAgentVersion('11.4.0.0')
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();
    await render(hbs `
      <div id='modalDestination'></div>
      {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    assert.equal(findAll('.host-details_dropdown-action-list li').length, 4, 'No Network isolation related options rendered');
  });

  test('test for absence of network isolation options when policy is not present when version is 11.5 and above', async function(assert) {
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .machineIdentityAgentVersion('11.5.0.0')
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();
    await render(hbs `
      <div id='modalDestination'></div>
      {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    assert.equal(findAll('.host-details_dropdown-action-list li').length, 5, 'No Network isolation related options rendered');
  });

  test('test for network isolation options', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .policy(policy)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();
    await render(hbs `
      <div id='modalDestination'></div>
      {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    assert.equal(findAll('.rsa-dropdown-action-list li').length, 0, 'No Network isolation related options rendered');

    await triggerEvent(find('.host-details_dropdown-action-list li.isolate-button button'), 'mouseover');

    return settled().then(() => {
      assert.equal(findAll('.rsa-dropdown-action-list li').length, 2, '2 Network isolation related options rendered');
    });
  });

  test('test for machine isolation modal', async function(assert) {
    const isolationStatus = {
      isolated: false
    };
    assert.expect(2);
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .policy(policy)
      .isolationStatus(isolationStatus)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();

    await render(hbs `
      <div id='modalDestination'></div>
      {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    await triggerEvent(find('.host-details_dropdown-action-list li.isolate-button button'), 'mouseover');

    assert.equal(findAll('.machine-isolation').length, 0, 'isolation modal not loaded');
    await click(find('.machine-isolation-selector li button'));

    return settled().then(() => {
      assert.equal(findAll('#modalDestination .machine-isolation .isolation-modal-content').length, 1, 'isolation modal loaded');
    });

  });

  test('test for manual file download modal', async function(assert) {
    const isolationStatus = {
      isolated: false
    };
    assert.expect(2);
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .policy(policy)
      .isolationStatus(isolationStatus)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();

    await render(hbs `
      <div id='modalDestination'></div>
      {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    await triggerEvent(find('.host-details_dropdown-action-list li.manual-file-download-button'), 'mouseover');

    assert.equal(findAll('.manual-file-download').length, 0, 'Manual file download modal not loaded');
    await click(find('.manual-file-download-button button'));

    return settled().then(() => {
      assert.equal(findAll('#modalDestination .manual-file-download').length, 1, 'Manual file download modal loaded');
    });

  });


  test('test for Release isolation modal', async function(assert) {
    const isolationStatus = {
      isolated: true,
      comment: 'Test comment',
      excludedIps: []
    };
    assert.expect(3);
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .policy(policy)
      .isolationStatus(isolationStatus)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();

    await render(hbs `
      <div id='modalDestination'></div>
      {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    await triggerEvent(find('.host-details_dropdown-action-list li.isolate-button button'), 'mouseover');

    assert.equal(findAll('.machine-isolation').length, 0, 'isolation modal not loaded');
    await click(find('.machine-isolation-selector li button'));

    return settled().then(() => {
      assert.equal(findAll('#modalDestination .machine-isolation .isolation-modal-content').length, 0, 'isolation modal loaded');
      assert.equal(findAll('#modalDestination .machine-isolation .release-modal-content').length, 1, 'isolation modal loaded');
    });

  });

  test('test for Edit isolation modal', async function(assert) {
    const isolationStatus = {
      isolated: true,
      comment: 'Test comment',
      excludedIps: []
    };
    assert.expect(4);
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .policy(policy)
      .isolationStatus(isolationStatus)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();

    await render(hbs `
      <div id='modalDestination'></div>
      {{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');

    await triggerEvent(find('.host-details_dropdown-action-list li.isolate-button button'), 'mouseover');

    assert.equal(findAll('.machine-isolation').length, 0, 'isolation modal not loaded');
    await click(find('.machine-isolation-selector li:last-child button'));

    return settled().then(() => {
      assert.equal(findAll('#modalDestination .machine-isolation .isolation-modal-content').length, 0, 'isolation modal not loaded');
      assert.equal(findAll('#modalDestination .machine-isolation .release-modal-content').length, 0, 'Release from isolation modal not loaded');
      assert.equal(findAll('#modalDestination .machine-isolation .edit-modal-content').length, 1, 'edit exclusion list modal loaded');
    });

  });

  test('Test for More Actions when on RAR', async function(assert) {
    new ReduxDataHelper(setState)
      .hostOverview(data)
      .machineIdentityAgentVersion('11.4.0.0')
      .policy(policy)
      .lastSeen('RelayServer')
      .isJsonExportCompleted(true)
      .build();
    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    assert.equal(findAll('.host-details_dropdown-action-list li').length, 3, 'Number of actions present is 3 as MFT and system dump permission are not present');

    assert.ok(find('.host-start-scan-button'), 'scan-command renders giving the start scan button');
    assert.equal(findAll('.rsa-icon-check-shield').length, 0, 'Start scan icon not present');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(2)').textContent.trim(), 'Export Host details', 'Export Host details button renders');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(3)').textContent.trim(), 'Network Isolation', 'Network Isolation button renders');
  });

  test('Test for More Actions when on RAR when version is 11.5 and above', async function(assert) {
    new ReduxDataHelper(setState)
      .hostOverview(data)
      .machineIdentityAgentVersion('11.5.0.0')
      .policy(policy)
      .lastSeen('RelayServer')
      .isJsonExportCompleted(true)
      .build();
    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    assert.equal(findAll('.host-details_dropdown-action-list li').length, 4, 'Number of actions present is 3 as MFT and system dump permission are not present');

    assert.ok(find('.host-start-scan-button'), 'scan-command renders giving the start scan button');
    assert.equal(findAll('.rsa-icon-check-shield').length, 0, 'Start scan icon not present');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(2)').textContent.trim(), 'Export Host details', 'Export Host details button renders');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(3)').textContent.trim(), 'Network Isolation', 'Network Isolation button renders');
  });

});

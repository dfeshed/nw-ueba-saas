import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { click, find, findAll, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;
const data = {
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
  }
};

module('Integration | Component | host detail more-actions', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('test for More Actions', async function(assert) {
    new ReduxDataHelper(setState)
      .host(data)
      .isJsonExportCompleted(true)
      .build();
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', ['endpoint-server.agent.manage']);
    await render(hbs `{{host-detail/header/more-actions}}`);
    await click('.host_more_actions .host-details-more-actions');
    assert.equal(findAll('.host-details_dropdown-action-list li').length, 3, 'Number of actions present is 3 as MFT permission added');

    assert.ok(find('.host-start-scan-button'), 'scan-command renders giving the start scan button');
    assert.equal(findAll('.rsa-icon-check-shield-lined').length, 0, 'Start scan icon not present');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(2)').textContent.trim(), 'Export Host details', 'Export Host details button renders');
    assert.equal(find('.host-details_dropdown-action-list li:nth-child(3)').textContent.trim(), 'Request MFT download', 'Request MFT downloads button renders');
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
  });

  test('test for Request MFT', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .host(data)
      .hostOverview(data)
      .isJsonExportCompleted(true)
      .isSnapshotsAvailable(true)
      .agentId('A0351965-30D0-2201-F29B-FDD7FD32EB21')
      .build();
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', ['endpoint-server.agent.manage']);
    await render(hbs `{{host-detail/header/more-actions}}`);
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

    await click('.host-details_dropdown-action-list li.downloadMFT-button');
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
});

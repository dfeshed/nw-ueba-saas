import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | Policy Inspector | EDR Policy', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).build();
    await render(hbs`{{usm-policies/policies/inspector/edr-policy}}`);
    assert.equal(findAll('.usm-policies-inspector-edr').length, 1, 'The component appears in the DOM');
  });

  test('It shows the correct sections and properties', async function(assert) {
    new ReduxDataHelper(setState)
      .focusedPolicy({
        id: 'policy_014',
        policyType: 'edrPolicy',
        name: 'EMC Reston! 014',
        description: 'EMC Reston 014 of policy policy_014',
        dirty: false,
        defaultPolicy: false,
        lastPublishedOn: 1527489158739,
        createdBy: 'admin',
        createdOn: 1540318426092,
        lastModifiedBy: 'admin',
        lastModifiedOn: 1540318426092,
        associatedGroups: [
          {
            referenceId: '5b7d886500319b5520f4b67d',
            name: 'Group 01'
          },
          {
            referenceId: '5b7d886500319b5520f4b672',
            name: 'Group 02'
          }
        ],
        scanType: 'SCHEDULED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: 1,
        recurrenceUnit: 'WEEKS',
        runOnDaysOfWeek: ['WEDNESDAY'],
        cpuMax: 75,
        cpuMaxVm: 85,
        captureFloatingCode: true,
        downloadMbr: false,
        filterSignedHooks: false,
        requestScanOnRegistration: true,
        blockingEnabled: false,
        primaryAddress: '10.10.10.10',
        primaryNwServiceId: 'id1',
        primaryHttpsPort: 443,
        primaryHttpsBeaconInterval: 3,
        primaryHttpsBeaconIntervalUnit: 'HOURS',
        primaryUdpPort: 444,
        primaryUdpBeaconInterval: 3,
        primaryUdpBeaconIntervalUnit: 'MINUTES',
        agentMode: 'FULL_MONITORING'
      }
    )
    .build();
    await render(hbs`{{usm-policies/policies/inspector/edr-policy}}`);
    assert.equal(findAll('.heading').length, 5, '5 headings are shown');
    assert.equal(findAll('.heading')[0].innerText, 'Scan Schedule', 'first heading is as expected');
    assert.equal(findAll('.value')[4].innerText, 'Enabled', 'capture floating code shows expected value');
    assert.equal(findAll('.title').length, 15, '15 property names are shown');
    assert.equal(findAll('.value').length, 15, '15 value elements are shown');
  });
});
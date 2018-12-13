import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | Policy Inspector', function(hooks) {
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
    await render(hbs`{{usm-policies/policies/inspector}}`);
    assert.equal(findAll('.usm-policies-inspector').length, 1, 'The component appears in the DOM');
  });

  test('It shows the common sections for history and groups', async function(assert) {
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
        scanType: 'ENABLED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: 1,
        recurrenceUnit: 'WEEKS',
        runOnDaysOfWeek: ['WEDNESDAY'],
        cpuMax: 75,
        cpuMaxVm: 85,
        // captureFloatingCode: true,
        downloadMbr: false,
        // filterSignedHooks: false,
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
        agentMode: 'ADVANCED'
      }).build();

    await render(hbs`{{usm-policies/policies/inspector}}`);
    assert.equal(findAll('.usm-policies-inspector .heading').length, 7, 'expected headings are shown');
    assert.equal(findAll('.usm-policies-inspector .heading')[6].innerText, 'History', 'history section is last as expected');
    assert.equal(findAll('.usm-policies-inspector .heading')[0].innerText, 'Applied to Group(s)', 'first heading is as expected');
    assert.equal(findAll('.usm-policies-inspector .title').length, 18, 'expected property names are shown');
    assert.equal(findAll('.usm-policies-inspector .value').length, 20, 'expected value elements are shown');
    assert.equal(findAll('.usm-policies-inspector .value')[15].innerText, '2018-10-23 02:13', 'created on value shows as expected');
    assert.equal(findAll('.usm-policies-inspector .value')[16].innerText, 'admin', 'created by value shows as expected');
    assert.equal(findAll('.usm-policies-inspector .value')[17].innerText, '2018-10-23 02:13', 'last updated on value shows as expected');
    assert.equal(findAll('.usm-policies-inspector .value')[18].innerText, 'admin', 'last updated by value shows as expected');
    assert.equal(findAll('.usm-policies-inspector .value')[19].innerText, '2018-05-28 02:32', 'last published on value shows as expected');
  });

  test('It does not show the groups section when no groups', async function(assert) {
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
        createdOn: 0,
        lastModifiedBy: 'admin',
        lastModifiedOn: 1540318426092,
        associatedGroups: [],
        scanType: 'ENABLED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: 1,
        recurrenceUnit: 'WEEKS',
        runOnDaysOfWeek: ['WEDNESDAY'],
        cpuMax: 75,
        cpuMaxVm: 85,
        // captureFloatingCode: true,
        downloadMbr: false,
        // filterSignedHooks: false,
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
        agentMode: 'ADVANCED'
      }).build();

    await render(hbs`{{usm-policies/policies/inspector}}`);
    assert.equal(findAll('.usm-policies-inspector .heading').length, 6, 'expected headings are shown');
    assert.equal(findAll('.usm-policies-inspector .heading')[5].innerText, 'History', 'history section is last as expected');
  });

  test('It shows the history properties with values', async function(assert) {
    new ReduxDataHelper(setState)
      .focusedPolicy({
        id: 'policy_014',
        policyType: 'edrPolicy',
        name: 'EMC Reston! 014',
        description: 'EMC Reston 014 of policy policy_014',
        dirty: false,
        defaultPolicy: false,
        lastPublishedOn: 0,
        createdBy: 'admin',
        createdOn: 0,
        lastModifiedBy: '',
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
        scanType: 'ENABLED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: 1,
        recurrenceUnit: 'WEEKS',
        runOnDaysOfWeek: ['WEDNESDAY'],
        cpuMax: 75,
        cpuMaxVm: 85,
        // captureFloatingCode: true,
        downloadMbr: false,
        // filterSignedHooks: false,
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
        agentMode: 'ADVANCED'
      }).build();

    await render(hbs`{{usm-policies/policies/inspector}}`);
    assert.equal(findAll('.usm-policies-inspector .heading').length, 7, 'expected headings are shown');
    assert.equal(findAll('.usm-policies-inspector .heading')[6].innerText, 'History', 'history section is last as expected');
    assert.equal(findAll('.usm-policies-inspector .value')[15].innerText, 'System Created', 'created on value shows as expected');
    assert.equal(findAll('.usm-policies-inspector .value')[16].innerText, 'admin', 'created by value shows as expected');
    assert.equal(findAll('.usm-policies-inspector .value')[17].innerText, '2018-10-23 02:13', 'last updated on value shows as expected');
    assert.equal(findAll('.usm-policies-inspector .lastModifiedBy').length, 0, 'last modified by is missing as expected');
    assert.equal(findAll('.usm-policies-inspector .lastPublishedOn').length, 0, 'last published on is missing as expected');
  });
});
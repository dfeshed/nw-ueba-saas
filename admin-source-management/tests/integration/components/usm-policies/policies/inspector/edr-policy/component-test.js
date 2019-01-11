import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, triggerEvent } from '@ember/test-helpers';
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
        scanType: 'ENABLED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: 1,
        recurrenceUnit: 'WEEKS',
        runOnDaysOfWeek: ['WEDNESDAY'],
        cpuMax: 75,
        cpuMaxVm: 85,
        // captureFloatingCode: true,
        scanMbr: false,
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

    await render(hbs`{{usm-policies/policies/inspector/edr-policy}}`);
    assert.equal(findAll('.heading').length, 5, '5 headings are shown');
    assert.equal(findAll('.heading')[0].innerText, 'Scan Schedule', 'first heading is as expected');
    // assert.equal(findAll('.value')[4].innerText, 'Enabled', 'capture floating code shows expected value');
    assert.equal(findAll('.title').length, 13, '13 property names are shown');
    assert.equal(findAll('.value').length, 13, '13 value elements are shown');
  });

  test('Custom config setting and its tooltip is shown', async function(assert) {
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
        scanMbr: false,
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
        agentMode: 'ADVANCED',
        customConfig: '"cpuMax" : 90,"cpuMaxVm" : 90,"scanMbr" : false,"blockingEnabled" : false,"requestScanOnRegistration" : false,"primaryHttpsPort" : 443,"primaryHttpsBeaconInterval" : 15,"primaryHttpsBeaconIntervalUnit" : "MINUTES","primaryUdpPort" : 444,"primaryUdpBeaconInterval" : 30,"primaryUdpBeaconIntervalUnit" : "SECONDS","agentMode" : "ADVANCED","offlineDiskStorageSizeInMb" : 100,"policyType" : "edrPolicy","name" : "Default EDR Policy","description" : "These are the settings that are applied when not defined in another policy applied to an agent.","dirty" : false,"defaultPolicy" : true,'
      }).build();

    const expectedCustomSetting = '"cpuMax" : 90,"cpuMaxVm" : 90,"scanMbr" : false,"blockingEnabled" : false,"requestScanOnRegistration" : false,"primaryHttpsPort" : 443,"primaryHttpsBeaconInterval" : 15,"primaryHttpsBeaconIntervalUnit" : "MINUTES","primaryUdpPort" : 444,"primaryUdpBeaconInterval" : 30,"primaryUdpBeaconIntervalUnit" : "SECONDS","agentMode" : "ADVANCED","offlineDiskStorageSizeInMb" : 100,"policyType" : "edrPolicy","name" : "Default EDR Policy","description" : "These are the settings that are applied when not defined in another policy applied to an agent.","dirty" : false,"defaultPolicy" : true,';
    await render(hbs`{{usm-policies/policies/inspector/edr-policy}}`);
    assert.equal(findAll('.heading').length, 6, '6 headings are shown');
    assert.equal(findAll('.heading')[5].innerText, 'Advanced Configuration', 'advanced setting heading as expected');
    assert.equal(findAll('.title').length, 14, '14 property names are shown');
    assert.equal(findAll('.value').length, 14, '14 value elements are shown');
    assert.equal(findAll('.value')[13].innerText.trim(),
      expectedCustomSetting,
      'custom config value is as expected');
    await triggerEvent('.value .tooltip-text', 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[0].innerText.trim(),
      `${expectedCustomSetting.substring(0, 253)}...`,
      'custom setting tooltip is as expected');
  });

  test('Default endpoint server and its tooltip is shown for default EDR policy', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    new ReduxDataHelper(setState)
      .focusedPolicy({
        id: '__default_edr_policy',
        policyType: 'edrPolicy',
        name: 'Default EDR Policy',
        description: 'Default EDR Policy __default_edr_policy',
        dirty: false,
        defaultPolicy: true,
        lastPublishedCopy: {},
        lastPublishedOn: 1540318459759,
        createdOn: 0,
        lastModifiedOn: 0,
        associatedGroups: [],
        scanType: 'ENABLED',
        scanStartDate: '2018-09-09',
        scanStartTime: '10:23',
        recurrenceInterval: 1,
        recurrenceUnit: 'DAYS',
        runOnDaysOfWeek: null,
        cpuMax: 75,
        cpuMaxVm: 85,
        // captureFloatingCode: false,
        scanMbr: false,
        // filterSignedHooks: false,
        requestScanOnRegistration: false,
        blockingEnabled: false,
        primaryAddress: '',
        primaryNwServiceId: '',
        primaryHttpsPort: 443,
        primaryHttpsBeaconInterval: null,
        primaryHttpsBeaconIntervalUnit: null,
        primaryUdpPort: 444,
        primaryUdpBeaconInterval: null,
        primaryUdpBeaconIntervalUnit: null,
        agentMode: 'INSIGHTS'
      }).build();

    const expectedDefaultEndpointServer = translation.t('adminUsm.policies.detail.defaultPrimaryAddress');
    const expectedDefaultEndpointTooltip = translation.t('adminUsm.policies.detail.defaultPrimaryAddressTooltip');
    await render(hbs`{{usm-policies/policies/inspector/edr-policy}}`);
    assert.equal(findAll('.heading').length, 5, '5 headings are shown');
    assert.equal(findAll('.default-host-name')[0].innerText.trim(),
      expectedDefaultEndpointServer, 'default endpoint server value is as expected');
    await triggerEvent(document.querySelectorAll('.tooltip-text')[0], 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[0].innerText.trim(),
      expectedDefaultEndpointTooltip,
      'default endpoint tooltip is as expected');
  });
});
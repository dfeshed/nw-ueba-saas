import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

let setState;

const testPolicy = {
  policy: {
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
  },
  origins: {
    'createdOn': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'lastModifiedOn': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'id': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'policyType': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'name': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'description': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'dirty': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'defaultPolicy': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'lastPublishedOn': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'scanType': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'scanStartTime': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'recurrenceInterval': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'recurrenceUnit': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'runOnDaysOfWeek': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'cpuMax': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'cpuMaxVm': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'scanMbr': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'blockingEnabled': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'requestScanOnRegistration': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'primaryHttpsPort': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'primaryHttpsBeaconInterval': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'primaryHttpsBeaconIntervalUnit': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'primaryUdpPort': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'primaryUdpBeaconInterval': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'primaryUdpBeaconIntervalUnit': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'agentMode': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    },
    'offlineDiskStorageSizeInMb': {
      'groupName': 'test',
      'policyName': 'test',
      'conflict': false
    }
  }
};

module('Integration | Component | group-ranking/inspector | EDR Policy', function(hooks) {
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
    await render(hbs`{{usm-groups/group-ranking/inspector/edr-policy}}`);
    assert.equal(findAll('.usm-policies-inspector-edr').length, 1, 'The component appears in the DOM');
  });

  test('It shows the correct sections and properties', async function(assert) {
    new ReduxDataHelper(setState)
      .focusedPolicy(testPolicy)
      .build();
    const primaryUdpBeaconInterval = '3 Minutes';
    await render(hbs`{{usm-groups/group-ranking/inspector/edr-policy}}`);
    assert.equal(findAll('.heading').length, 5, '5 headings are shown');
    assert.equal(findAll('.heading .col-md-7')[0].innerText, 'Scan Schedule', 'first heading first part is as expected');
    assert.equal(findAll('.heading .col-md-5')[0].innerText, 'GOVERNING POLICY - GROUP', 'first heading drcond part is as expected');
    assert.equal(findAll('.title').length, 13, '13 property names are shown');
    assert.equal(findAll('.value').length, 36, '36 value elements are shown');
    assert.equal(findAll('.value')[34].innerText.trim(), primaryUdpBeaconInterval, 'primaryUdpBeaconInterval config value is as expected');
  });

});
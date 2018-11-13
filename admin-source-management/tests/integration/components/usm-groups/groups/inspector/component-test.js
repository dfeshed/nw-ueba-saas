import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | Group Inspector', function(hooks) {
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
    await render(hbs`{{usm-groups/groups/inspector}}`);
    assert.equal(findAll('.usm-groups-inspector').length, 1, 'The component appears in the DOM');
  });

  test('It shows the common sections for history and applied policies', async function(assert) {
    new ReduxDataHelper(setState)
      .focusedGroup({
        'id': 'group_001',
        'name': 'Zebra 001',
        'description': 'Zebra 001 of group group_001',
        'createdBy': 'admin',
        'createdOn': 1523655354337,
        'dirty': false,
        'lastPublishedCopy': null,
        'lastPublishedOn': 0,
        'lastModifiedBy': 'admin',
        'lastModifiedOn': 1523655354337,
        'sourceCount': 10,
        'assignedPolicies': {
          'edrPolicy': {
            'referenceId': 'policy_001',
            'name': 'EMC 001'
          }
        },
        groupCriteria: {
          conjunction: 'AND',
          criteria: [
            [
              'osType',
              'IN',
              [
                'Linux'
              ]
            ]
          ]
        }
      }
    )
    .build();
    await render(hbs`{{usm-groups/groups/inspector}}`);
    assert.equal(findAll('.usm-groups-inspector .heading').length, 4, 'expected headings are shown');
    assert.equal(findAll('.usm-groups-inspector .heading')[0].innerText, 'Policy(ies) Applied', 'first heading is as expected');
    assert.equal(findAll('.usm-groups-inspector .heading')[3].innerText, 'History', 'History is the last section as expected');
    assert.equal(findAll('.usm-groups-inspector .title').length, 6, 'expected property names are shown');
    assert.equal(findAll('.usm-groups-inspector .group-criteria')[0].innerText,
      'Sources included if\nall\nof the following criteria are met:',
      'expected AND conjunction property');
    assert.equal(findAll('.usm-groups-inspector .value').length, 7, 'expected value elements are shown');
    assert.equal(findAll('.usm-groups-inspector .value')[3].innerText, '2018-04-13 05:35', 'created on value shows as expected');
    assert.equal(findAll('.usm-groups-inspector .value')[4].innerText, 'admin', 'created by value shows as expected');
    assert.equal(findAll('.usm-groups-inspector .value')[5].innerText, '2018-04-13 05:35', 'last updated on value shows as expected');
    assert.equal(findAll('.usm-groups-inspector .value')[6].innerText, 'admin', 'last updated by value shows as expected');
    assert.equal(findAll('.usm-groups-inspector .value')[1].innerText, '10', 'source count shows as expected');
    assert.equal(findAll('.usm-groups-inspector .lastPublishedOn').length, 0, 'last published on value is not shown as expected');
  });

  test('It does not show the applied policies section when no assigned policies', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    new ReduxDataHelper(setState)
      .focusedGroup({
        'id': 'group_005',
        'name': 'Volleyball 005',
        'description': 'Volleyball 005 of group group_005',
        'createdBy': 'admin',
        'createdOn': 1523655354337,
        'dirty': false,
        'lastPublishedCopy': null,
        'lastPublishedOn': 1523655354337,
        'lastModifiedBy': 'admin',
        'lastModifiedOn': 1523655354337,
        'assignedPolicies': {},
        'sourceCount': -1,
        'groupCriteria': {
          'conjunction': 'OR',
          'criteria': [
            [
              'ipv4',
              'BETWEEN',
              [
                '10.40.14.0',
                '10.40.14.255'
              ]
            ],
            [
              'ipv4',
              'BETWEEN',
              [
                '10.40.68.0',
                '10.40.68.255'
              ]
            ]
          ]
        }
      }
    )
    .build();
    await render(hbs`{{usm-groups/groups/inspector}}`);
    assert.equal(findAll('.usm-groups-inspector .heading').length, 3, 'expected headings are shown');
    assert.equal(findAll('.usm-groups-inspector .heading')[2].innerText, 'History', 'History is the last section as expected');
    assert.equal(findAll('.usm-groups-inspector .group-criteria')[0].innerText,
      'Sources included if\nany\nof the following criteria are met:',
      'expected ANY conjunction property');
    const expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountPublishedNewGroupTooltip');
    assert.equal(findAll('.usm-groups-inspector .value')[0].innerText, expectedSrcCount.string, 'source count shows as expected');
  });

  test('It shows the history properties with values', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    new ReduxDataHelper(setState)
      .focusedGroup({
        'id': 'group_004',
        'name': 'Wonder Woman 004',
        'description': 'Wonder Woman 004 of group group_004',
        'createdBy': '',
        'createdOn': 1523655354337,
        'dirty': false,
        'lastPublishedCopy': null,
        'lastPublishedOn': 0,
        'lastModifiedBy': '',
        'lastModifiedOn': 0,
        'sourceCount': -2,
        'assignedPolicies': {},
        groupCriteria: {
          conjunction: 'AND',
          criteria: [
            [
              'osType',
              'IN',
              [
                'Windows'
              ]
            ],
            [
              'osType',
              'IN',
              [
                'Windows'
              ]
            ],
            [
              'osDescription',
              'ENDS_WITH',
              [
                'hebjc'
              ]
            ],
            [
              'ipv4',
              'NOT_IN',
              [
                '125.1.1.227,125.1.1.78\n'
              ]
            ],
            [
              'hostname',
              'EQUAL',
              [
                'trbkx'
              ]
            ],
            [
              'osDescription',
              'CONTAINS',
              [
                'xltbk'
              ]
            ],
            [
              'ipv4',
              'NOT_BETWEEN',
              [
                '1.1.1.45',
                '1.1.2.193'
              ]
            ]
          ]
        }
      }
    )
    .build();
    await render(hbs`{{usm-groups/groups/inspector}}`);
    assert.equal(findAll('.usm-groups-inspector .heading').length, 3, 'expected headings are shown');
    assert.equal(findAll('.usm-groups-inspector .title').length, 8, 'expected titles shown');
    assert.equal(findAll('.usm-groups-inspector .value').length, 9, 'expected values shown');
    assert.equal(findAll('.usm-groups-inspector .value')[8].innerText, '2018-04-13 05:35', 'created on value shows as expected');
    assert.equal(findAll('.usm-groups-inspector .createdBy').length, 0, 'created by value not shown as expected');
    assert.equal(findAll('.usm-groups-inspector .lastModifiedOn').length, 0, 'last updated on value not shown as expected');
    assert.equal(findAll('.usm-groups-inspector .lastModifiedBy').length, 0, 'last updated by is missing as expected');
    assert.equal(findAll('.usm-groups-inspector .lastPublishedOn').length, 0, 'last published on is missing as expected');
    const expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountPublishedNoEndpointTooltip');
    assert.equal(findAll('.usm-groups-inspector .value')[0].innerText, expectedSrcCount.string, 'source count shows as expected');
  });

  test('It shows the source count when special case', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    new ReduxDataHelper(setState)
      .focusedGroup({
        'id': 'group_005',
        'name': 'Volleyball 005',
        'description': 'Volleyball 005 of group group_005',
        'createdBy': 'admin',
        'createdOn': 1523655354337,
        'dirty': false,
        'lastPublishedCopy': null,
        'lastPublishedOn': 1523655354337,
        'lastModifiedBy': 'admin',
        'lastModifiedOn': 1523655354337,
        'assignedPolicies': {},
        'sourceCount': -3,
        'groupCriteria': {
          'conjunction': 'OR',
          'criteria': [
            [
              'ipv4',
              'BETWEEN',
              [
                '10.40.14.0',
                '10.40.14.255'
              ]
            ],
            [
              'ipv4',
              'BETWEEN',
              [
                '10.40.68.0',
                '10.40.68.255'
              ]
            ]
          ]
        }
      }
    )
    .build();
    await render(hbs`{{usm-groups/groups/inspector}}`);
    const expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountUnpublishedGroupTooltip');
    assert.equal(findAll('.usm-groups-inspector .value')[0].innerText, expectedSrcCount.string, 'source count shows as expected');
  });
});
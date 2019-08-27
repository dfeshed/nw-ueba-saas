import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | usm-groups/group-ranking/edit-ranking-step/row', function(hooks) {
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
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithData()
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step/row}}`);
    assert.equal(findAll('tr').length, 1, 'The component appears in the DOM');
  });

  test('The component mouseUp on the row', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithData()
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step/row}}`);
    await triggerEvent(document.querySelectorAll('tr')[0], 'mouseUp');
    assert.equal(findAll('tr.is-selected').length, 1, 'The row is selected');
  });

  test('Show correct source count for special cases', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const rankingData = [
      {
        'id': 'gggg_001',
        'name': 'Zebra 001',
        'description': 'Zebra 001 of group group_001',
        'createdBy': 'local',
        'createdOn': 1523655354337,
        'dirty': false,
        'lastPublishedCopy': null,
        'lastPublishedOn': 1523655354337,
        'lastModifiedBy': 'local',
        'lastModifiedOn': 1523655354337,
        'sourceCount': -1,
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
      },
      {
        id: 'group_002',
        'name': 'Awesome! 012',
        'description': 'Awesome! 012 of group group_012',
        'createdBy': 'local',
        'createdOn': 1523655368173,
        'dirty': true,
        'lastPublishedCopy': null,
        'lastPublishedOn': 1523655368173,
        'lastModifiedBy': 'local',
        'lastModifiedOn': 1523655368173,
        'sourceCount': 30,
        'assignedPolicies': {},
        groupCriteria: {
          conjunction: 'AND',
          criteria: [
            [
              'ipv4',
              'BETWEEN',
              [
                '123',
                '22'
              ]
            ]
          ]
        }
      },
      {
        id: 'group_002',
        'name': 'Awesome! 012',
        'description': 'Awesome! 012 of group group_012',
        'createdBy': 'local',
        'createdOn': 1523655368173,
        'dirty': true,
        'lastPublishedCopy': null,
        'lastPublishedOn': 1523655368173,
        'lastModifiedBy': 'local',
        'lastModifiedOn': 1523655368173,
        'sourceCount': -2,
        'assignedPolicies': {},
        groupCriteria: {
          conjunction: 'AND',
          criteria: [
            [
              'ipv4',
              'BETWEEN',
              [
                '123',
                '22'
              ]
            ]
          ]
        }
      },
      {
        'id': 'group_003',
        'name': 'Xylaphone 003',
        'description': 'Xylaphone 003 of group group_003',
        'createdBy': 'local',
        'createdOn': 1523655354337,
        'dirty': true,
        'lastPublishedCopy': null,
        'lastPublishedOn': 0,
        'lastModifiedBy': 'local',
        'lastModifiedOn': 1523655354337,
        'sourceCount': -3,
        'assignedPolicies': {},
        'groupCriteria': {
          'conjunction': 'AND',
          'criteria': [
            ['osType', 'IN', []]
          ]
        }
      },
      {
        'id': 'group_013',
        'name': 'Tom n Jerry 013',
        'description': 'Tom n Jerry 013 of group group_013',
        'createdBy': 'local',
        'createdOn': 1523655354337,
        'dirty': false,
        'lastPublishedCopy': null,
        'lastPublishedOn': 1523655354337,
        'lastModifiedBy': 'local',
        'lastModifiedOn': 1523655354337,
        'sourceCount': 10,
        'assignedPolicies': {},
        'groupCriteria': {
          'conjunction': 'AND',
          'criteria': [
            ['osType', 'IN', []]
          ]
        }
      }
    ];
    new ReduxDataHelper(setState).groupRankingWithData(rankingData).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('.group-ranking-table-body tr').length, 5, '5 groups are showing');

    let expectedSrcCountTip = translation.t('adminUsm.groups.list.sourceCountPublishedNewGroupTooltip');
    assert.equal(document.querySelectorAll('.src-count')[0].innerText.trim(), 'Updating', '-1 source count as expected');
    await triggerEvent(document.querySelectorAll('.src-count-text')[0], 'mouseEnter');
    assert.equal(document.querySelectorAll('.src-count-tip')[0].innerText.trim(), expectedSrcCountTip.string, '-1 source count tooltip as expected');

    expectedSrcCountTip = translation.t('adminUsm.groups.list.sourceCountUnpublishedEditedGroupTooltip');
    assert.equal(document.querySelectorAll('.src-count')[1].innerText.trim(), 30, 'unpublished edit source count as expected');
    await triggerEvent(document.querySelectorAll('.src-count-text')[1], 'mouseEnter');
    assert.equal(document.querySelectorAll('.src-count-tip')[1].innerText.trim(), expectedSrcCountTip.string, 'unpublished edit count tooltip as expected');

    expectedSrcCountTip = translation.t('adminUsm.groups.list.sourceCountPublishedNoEndpointTooltip');
    assert.equal(document.querySelectorAll('.src-count')[2].innerText.trim(), 'N/A', '-2 source count as expected');
    await triggerEvent(document.querySelectorAll('.src-count-text')[2], 'mouseEnter');
    assert.equal(document.querySelectorAll('.src-count-tip')[2].innerText.trim(), expectedSrcCountTip.string, '-2 source count tooltip as expected');

    expectedSrcCountTip = translation.t('adminUsm.groups.list.sourceCountUnpublishedNewGroupTooltip');
    assert.equal(document.querySelectorAll('.src-count')[3].innerText.trim(), 'N/A', '-3 source count as expected');
    await triggerEvent(document.querySelectorAll('.src-count-text')[3], 'mouseEnter');
    assert.equal(document.querySelectorAll('.src-count-tip')[3].innerText.trim(), expectedSrcCountTip.string, '-3 source count tooltip as expected');

    assert.equal(document.querySelectorAll('.src-count')[4].innerText.trim(), 10, 'published and synced source count as expected');
    assert.equal(document.querySelectorAll('.src-count-text').length, 4, 'no tooltip rendered for normal count');
  });
  test('Show edrPolicy policy applied', async function(assert) {
    const rankingData =
    {
      'id': 'gggg_001',
      'name': 'Zebra 001',
      'description': 'Zebra 001 of group group_001',
      'assignedPolicies': {
        'edrPolicy': {
          'referenceId': 'policy_002',
          'name': 'policy_edr'
        },
        'windowsLogPolicy': {
          'referenceId': 'policy_WL001',
          'name': 'policy_wind'
        }
      }
    };
    new ReduxDataHelper(setState).groupRankingWithData(rankingData).build();
    const selectedSourceType = 'edrPolicy';
    this.set('selectedSourceType', selectedSourceType);
    const item = rankingData;
    this.set('item', item);
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step/row selectedSourceType=selectedSourceType item=item}}`);
    assert.equal(document.querySelectorAll('.policy-cell')[0].innerText.trim(), 'policy_edr', 'edrPolicy shows as expected');
  });
  test('Show windowsLogPolicy policy applied', async function(assert) {
    const rankingData =
    {
      'id': 'gggg_001',
      'name': 'Zebra 001',
      'description': 'Zebra 001 of group group_001',
      'assignedPolicies': {
        'edrPolicy': {
          'referenceId': 'policy_002',
          'name': 'policy_edr'
        },
        'windowsLogPolicy': {
          'referenceId': 'policy_WL001',
          'name': 'policy_wind'
        }
      }
    };
    new ReduxDataHelper(setState).groupRankingWithData(rankingData).build();
    const selectedSourceType = 'windowsLogPolicy';
    this.set('selectedSourceType', selectedSourceType);
    const item = rankingData;
    this.set('item', item);
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step/row selectedSourceType=selectedSourceType item=item}}`);
    assert.equal(document.querySelectorAll('.policy-cell')[0].innerText.trim(), 'policy_wind', 'windowsLogPolicy shows as expected');
  });
});
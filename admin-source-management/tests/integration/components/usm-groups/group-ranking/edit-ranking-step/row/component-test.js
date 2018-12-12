import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, click, triggerEvent } from '@ember/test-helpers';
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

  test('The component click on the row', async function(assert) {
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupRankingWithData()
      .build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step/row}}`);
    await click('tr');
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
    assert.equal(findAll('.edit-ranking-step tr').length, 6, '5 groups are showing');

    let expectedSrcCountTip = translation.t('adminUsm.groups.list.sourceCountPublishedNewGroupTooltip');
    assert.equal(document.querySelector('.edit-ranking-step table').rows[1].cells[4].innerText.trim(), 'Updating', '-1 source count as expected');
    await triggerEvent(document.querySelectorAll('.tooltip-text')[0], 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[0].innerText.trim(), expectedSrcCountTip.string, '-1 source count tooltip as expected');

    expectedSrcCountTip = translation.t('adminUsm.groups.list.sourceCountUnpublishedEditedGroupTooltip');
    assert.equal(document.querySelector('.edit-ranking-step table').rows[2].cells[4].innerText.trim(), 30, 'unpublished edit source count as expected');
    await triggerEvent(document.querySelectorAll('.tooltip-text')[1], 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[1].innerText.trim(), expectedSrcCountTip.string, 'unpublished edit count tooltip as expected');

    expectedSrcCountTip = translation.t('adminUsm.groups.list.sourceCountPublishedNoEndpointTooltip');
    assert.equal(document.querySelector('.edit-ranking-step table').rows[3].cells[4].innerText.trim(), 'N/A', '-2 source count as expected');
    await triggerEvent(document.querySelectorAll('.tooltip-text')[2], 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[2].innerText.trim(), expectedSrcCountTip.string, '-2 source count tooltip as expected');

    expectedSrcCountTip = translation.t('adminUsm.groups.list.sourceCountUnpublishedNewGroupTooltip');
    assert.equal(document.querySelector('.edit-ranking-step table').rows[4].cells[4].innerText.trim(), 'N/A', '-3 source count as expected');
    await triggerEvent(document.querySelectorAll('.tooltip-text')[3], 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[3].innerText.trim(), expectedSrcCountTip.string, '-3 source count tooltip as expected');

    assert.equal(document.querySelector('.edit-ranking-step table').rows[5].cells[4].innerText.trim(), 10, 'published and synced source count as expected');
    assert.ok(document.querySelectorAll('.tooltip-text').length === 4, 'no tooltip rendered for normal count');
  });
});
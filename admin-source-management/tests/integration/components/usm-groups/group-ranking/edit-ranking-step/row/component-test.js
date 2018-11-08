import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, click } from '@ember/test-helpers';
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
      }
    ];
    new ReduxDataHelper(setState).groupRankingWithData(rankingData).build();
    await render(hbs`{{usm-groups/group-ranking/edit-ranking-step}}`);
    assert.equal(findAll('.edit-ranking-step tr').length, 4, '3 groups are showing');

    let expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountPublishedNewGroupTooltip');
    assert.equal(document.querySelector('.edit-ranking-step table').rows[1].cells[4].innerText.trim(), expectedSrcCount.string, 'first source count as expected');
    expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountPublishedNoEndpointTooltip');
    assert.equal(document.querySelector('.edit-ranking-step table').rows[2].cells[4].innerText.trim(), expectedSrcCount.string, 'second source count as expected');
    expectedSrcCount = translation.t('adminUsm.groups.list.sourceCountUnpublishedGroupTooltip');
    assert.equal(document.querySelector('.edit-ranking-step table').rows[3].cells[4].innerText.trim(), expectedSrcCount.string, 'third source count as expected');
  });
});

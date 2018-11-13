import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import {
  focusedGroup,
  focusedGroupCriteria
} from 'admin-source-management/reducers/usm/group-details/group-selectors';

module('Unit | Selectors | Group Details | Group Selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('focusedGroupCriteria selector', function(assert) {
    const state = {
      usm: {
        groups: {
          focusedItem: {
            'id': 'group_007',
            'name': 'Football 007',
            'description': 'Football 007 of group group_007',
            'createdBy': 'admin',
            'createdOn': 1523655368173,
            'dirty': true,
            'lastPublishedCopy': null,
            'lastPublishedOn': 0,
            'lastModifiedBy': 'admin',
            'lastModifiedOn': 1523655368173,
            'assignedPolicies': {},
            'sourceCount': 5,
            'groupCriteria': {
              'conjunction': 'AND',
              'criteria': [
                [
                  'ipv4',
                  'IN',
                  [
                    '10.40.14.101,10.40.14.108,10.40.14.123,10.40.14.171'
                  ]
                ]
              ]
            }
          }
        }
      }
    };
    assert.expect(4);
    const groupForDetails = focusedGroup(Immutable.from(state));
    const newGroupCriteria = focusedGroupCriteria(Immutable.from(state), groupForDetails);
    assert.equal(newGroupCriteria.length, 1, '1 condition returned');
    assert.equal(newGroupCriteria[0][0], 'ipv4', 'correct attribute returned');
    assert.equal(newGroupCriteria[0][1], 'IN', 'correct operator returned');
    assert.equal(newGroupCriteria[0][2].trim(), '10.40.14.101, 10.40.14.108, 10.40.14.123, 10.40.14.171',
      'correct value returned');
  });

  test('focusedGroupCriteria selector, between and not between operators', function(assert) {
    const state = {
      usm: {
        groups: {
          focusedItem: {
            'id': 'group_013',
            'name': 'Tom n Jerry 013',
            'description': 'Tom n Jerry 013 of group group_013',
            'createdBy': 'admin',
            'createdOn': 1523655354337,
            'dirty': false,
            'lastPublishedCopy': null,
            'lastPublishedOn': 1523655354337,
            'lastModifiedBy': 'admin',
            'lastModifiedOn': 1523655354337,
            'assignedPolicies': {},
            'sourceCount': 10,
            'groupCriteria': {
              'conjunction': 'OR',
              'criteria': [
                [
                  'ipv4',
                  'BETWEEN',
                  [
                    '10.40.68.0',
                    '10.40.68.255'
                  ]
                ],
                [
                  'ipv4',
                  'NOT_BETWEEN',
                  [
                    '10.40.200.0',
                    '10.40.200.255'
                  ]
                ]
              ]
            }
          }
        }
      }
    };
    assert.expect(7);
    const groupForDetails = focusedGroup(Immutable.from(state));
    const newGroupCriteria = focusedGroupCriteria(Immutable.from(state), groupForDetails);
    assert.equal(newGroupCriteria.length, 2, '2 conditions returned');
    assert.equal(newGroupCriteria[0][0], 'ipv4', 'first correct attribute returned');
    assert.equal(newGroupCriteria[0][1], 'BETWEEN', 'first correct operator returned');
    assert.equal(newGroupCriteria[0][2].trim(), '10.40.68.0 and 10.40.68.255',
      'first correct value returned');
    assert.equal(newGroupCriteria[1][0], 'ipv4', 'second correct attribute returned');
    assert.equal(newGroupCriteria[1][1], 'NOT_BETWEEN', 'second correct operator returned');
    assert.equal(newGroupCriteria[1][2].trim(), '10.40.200.0 and 10.40.200.255',
      'second correct value returned');
  });
});
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import { module, test } from 'qunit';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/group-wizard-reducers';

const groupWizInitialState = new ReduxDataHelper().groupWiz().build().usm.groupWizard;

const initialState = Immutable.from({
  selectedGroupRanking: 'dfg',
  groupRankingOrig: [{ name: 'ijk' }, { name: 'lmn' }],
  groupRanking: [{ name: 'abc' }, { name: 'dfg' }],
  criteriaCache: [
    ['osType', 'IN', ['abc']]
  ],
  group: {
    name: 'test',
    groupCriteria: {
      conjunction: 'AND',
      criteria: [
        ['osType', 'IN', ['abc']]
      ]
    }
  }
});

module('Unit | Reducers | Group Wizard Reducers', function() {

  test('should return the initial state', function(assert) {
    const endState = reducers(undefined, {});
    assert.deepEqual(endState, groupWizInitialState);
  });

  test('on ADD_OR_OPERATOR, set to OR', function(assert) {
    const initialState2 = Immutable.from({
      criteriaCache: [
        ['osType', 'IN', ['abc']]
      ],
      group: {
        name: 'test',
        groupCriteria: {
          conjunction: 'AND',
          criteria: [
            ['osType', 'IN', ['abc']]
          ]
        }
      }
    });
    const expectedResult = {
      ...initialState2.group,
      groupCriteria: {
        ...initialState2.group.groupCriteria,
        conjunction: 'OR'
      }
    };
    const action = {
      type: ACTION_TYPES.ADD_OR_OPERATOR,
      payload: { andOr: 'OR' }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on FETCH_GROUP_RANKING, get-all wait', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupRanking('wait')
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_GROUP_RANKING });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'group renking is not-set and groupRankingStatus is wait');
  });

  test('on FETCH_GROUP_RANKING, get-all complete', function(assert) {
    const fetchGroupPayload = {
      data: [
        {
          'id': 'group_001',
          'name': 'Zebra 001',
          'description': 'Zebra 001 of group group_001',
          'dirty': false
        }
      ]
    };
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupRanking('complete')
      .groupRankingWithData(fetchGroupPayload)
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_GROUP_RANKING,
      payload: { data: fetchGroupPayload }
    });
    const result = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(result.groupRanking, expectedEndState.groupRanking);
  });

  test('on REORDER_GROUP_RANKING, reorder', function(assert) {
    const expectedResult = {
      ...initialState,
      groupRanking: [1, 2, 3]
    };
    const action = {
      type: ACTION_TYPES.REORDER_GROUP_RANKING,
      payload: { groupRanking: [1, 2, 3] }
    };
    const result = reducers(initialState, action);
    assert.deepEqual(result, expectedResult);
  });

  test('on SELECT_GROUP_RANKING, name', function(assert) {
    const expectedResult = {
      ...initialState,
      selectedGroupRanking: 'abc'
    };
    const action = {
      type: ACTION_TYPES.SELECT_GROUP_RANKING,
      payload: { groupRankingName: 'abc' }
    };
    const result = reducers(initialState, action);
    assert.deepEqual(result, expectedResult);
  });

  test('on SET_TOP_RANKING, group', function(assert) {
    const expectedResult = {
      ...initialState,
      groupRanking: [{ name: 'dfg' }, { name: 'abc' } ],
      selectedGroupRanking: null
    };
    const action = {
      type: ACTION_TYPES.SET_TOP_RANKING
    };
    const result = reducers(initialState, action);
    assert.deepEqual(result, expectedResult);
  });

  test('on RESET_GROUP_RANKING, from orig', function(assert) {
    const expectedResult = {
      ...initialState,
      groupRanking: [{ name: 'ijk' }, { name: 'lmn' }],
      selectedGroupRanking: null
    };
    const action = {
      type: ACTION_TYPES.RESET_GROUP_RANKING
    };
    const result = reducers(initialState, action);
    assert.deepEqual(result, expectedResult);
  });

  test('on SAVE_GROUP_RANKING, save wait', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupRanking('wait')
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SAVE_GROUP_RANKING });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'group renking is not-set and groupRankingStatus is wait');
  });

  test('on SAVE_GROUP_RANKING, save error', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupRanking('error')
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.SAVE_GROUP_RANKING });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'group renking is not-set and groupRankingStatus is error');
  });

  test('on SAVE_GROUP_RANKING, save complete', function(assert) {
    const fetchGroupPayload = {
      data: { policyType: 'selectedSourceType', groupIds: ['abc'] }
    };
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupRanking('complete')
      .groupRankingWithData(fetchGroupPayload)
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SAVE_GROUP_RANKING,
      payload: { data: fetchGroupPayload }
    });
    const result = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(result.groupRanking, expectedEndState.groupRanking);
  });

  test('on ADD_OR_OPERATOR, set to AND', function(assert) {
    const initialState2 = Immutable.from({
      criteriaCache: [
        ['osType', 'IN', ['abc']]
      ],
      group: {
        name: 'test',
        groupCriteria: {
          conjunction: 'OR',
          criteria: [
            ['osType', 'IN', ['abc']]
          ]
        }
      }
    });
    const expectedResult = {
      ...initialState2.group,
      groupCriteria: {
        ...initialState2.group.groupCriteria,
        conjunction: 'AND'
      }
    };
    const action = {
      type: ACTION_TYPES.ADD_OR_OPERATOR,
      payload: { andOr: 'AND' }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on ADD_CRITERIA, find if new Criteria is added', function(assert) {
    const expectedResult = {
      ...initialState.group,
      groupCriteria: {
        ...initialState.group.groupCriteria,
        criteria: [['osType', 'IN', ['abc']], ['osType', 'IN', []]]
      }
    };
    const action = {
      type: ACTION_TYPES.ADD_CRITERIA
    };
    const result = reducers(initialState, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on ADD_CRITERIA, update attribute input to ipv4 in added criteria', function(assert) {
    const initialState2 = Immutable.from({
      criteriaCache: [
        ['osType', 'IN', ['abc']], ['osType', 'IN', []]
      ],
      group: {
        name: 'test',
        groupCriteria: {
          conjunction: 'AND',
          criteria: [['osType', 'IN', ['abc']], ['osType', 'IN', []]]
        }
      }
    });
    const expectedResult = {
      ...initialState2.group,
      groupCriteria: {
        ...initialState2.group.groupCriteria,
        criteria: [['osType', 'IN', ['abc']], ['ipv4', 'BETWEEN', []]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',1', value: 'ipv4', fieldIndex: 0 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on REMOVE_CRITERIA, remove first criteria', function(assert) {
    const initialState2 = Immutable.from({
      criteriaCache: [
        ['osType', 'IN', ['abc']], ['ipv4', 'BETWEEN', []]
      ],
      group: {
        name: 'test',
        groupCriteria: {
          conjunction: 'AND',
          criteria: [['osType', 'IN', ['abc']], ['ipv4', 'BETWEEN', []]]
        }
      }
    });
    const expectedResult = {
      ...initialState2.group,
      groupCriteria: {
        ...initialState2.group.groupCriteria,
        criteria: [['ipv4', 'BETWEEN', []]]
      }
    };
    const action = {
      type: ACTION_TYPES.REMOVE_CRITERIA,
      payload: { criteriaPath: ',0' }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on REMOVE_CRITERIA, remove second criteria', function(assert) {
    const initialState2 = Immutable.from({
      criteriaCache: [
        ['osType', 'IN', ['abc']], ['ipv4', 'BETWEEN', []]
      ],
      group: {
        name: 'test',
        groupCriteria: {
          conjunction: 'AND',
          criteria: [['osType', 'IN', ['abc']], ['ipv4', 'BETWEEN', []]]
        }
      }
    });
    const expectedResult = {
      ...initialState2.group,
      groupCriteria: {
        ...initialState2.group.groupCriteria,
        criteria: [['osType', 'IN', ['abc']]]
      }
    };
    const action = {
      type: ACTION_TYPES.REMOVE_CRITERIA,
      payload: { criteriaPath: ',1' }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, find correct osType first operator', function(assert) {
    const expectedResult = {
      ...initialState.group,
      groupCriteria: {
        ...initialState.group.groupCriteria,
        criteria: [['osType', 'IN', []]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: 'osType', fieldIndex: 0 }
    };
    const result = reducers(initialState, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, find correct osType input', function(assert) {
    const expectedResult = {
      ...initialState.group,
      groupCriteria: {
        ...initialState.group.groupCriteria,
        criteria: [['osType', 'IN', ['abc']]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: ['abc'], fieldIndex: 2 }
    };
    const result = reducers(initialState, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, find correct osDescription first operator', function(assert) {
    const expectedResult = {
      ...initialState.group,
      groupCriteria: {
        ...initialState.group.groupCriteria,
        criteria: [['osDescription', 'EQUAL', []]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: 'osDescription', fieldIndex: 0 }
    };
    const result = reducers(initialState, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, find correct hostname first operator', function(assert) {
    const expectedResult = {
      ...initialState.group,
      groupCriteria: {
        ...initialState.group.groupCriteria,
        criteria: [['hostname', 'EQUAL', []]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: 'hostname', fieldIndex: 0 }
    };
    const result = reducers(initialState, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, find correct ipv4 first operator', function(assert) {
    const expectedResult = {
      ...initialState.group,
      groupCriteria: {
        ...initialState.group.groupCriteria,
        criteria: [['ipv4', 'BETWEEN', []]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: 'ipv4', fieldIndex: 0 }
    };
    const result = reducers(initialState, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, ipv4 BETWEEN add to first input', function(assert) {
    const initialState2 = Immutable.from({
      criteriaCache: [
        ['ipv4', 'BETWEEN', []]
      ],
      group: {
        name: 'test',
        groupCriteria: {
          criteria: [
            ['ipv4', 'BETWEEN', []]
          ]
        }
      }
    });
    const expectedResult = [['ipv4', 'BETWEEN', ['123']]];
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: '123', fieldIndex: 10 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.criteriaCache, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, ipv4 BETWEEN add to second input', function(assert) {
    const initialState2 = Immutable.from({
      criteriaCache: [
        ['ipv4', 'BETWEEN', ['123']]
      ],
      group: {
        name: 'test',
        groupCriteria: {
          criteria: [
            ['ipv4', 'BETWEEN', ['123']]
          ]
        }
      }
    });
    const expectedResult = [['ipv4', 'BETWEEN', ['123', 'def']]];
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: 'def', fieldIndex: 11 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.criteriaCache, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, ipv4 BETWEEN add to second input with empty first input', function(assert) {
    const initialState2 = Immutable.from({
      criteriaCache: [
        ['ipv4', 'BETWEEN', []]
      ],
      group: {
        name: 'test',
        groupCriteria: {
          criteria: [
            ['ipv4', 'BETWEEN', []]
          ]
        }
      }
    });
    const expectedResult = [['ipv4', 'BETWEEN', [undefined, 'def']]];
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: 'def', fieldIndex: 11 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.criteriaCache, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, ipv4 edit first input', function(assert) {
    const initialState2 = Immutable.from({
      criteriaCache: [
        ['ipv4', 'BETWEEN', ['abc', 'def']]
      ],
      group: {
        name: 'test',
        groupCriteria: {
          criteria: [
            ['ipv4', 'BETWEEN', ['abc', 'def']]
          ]
        }
      }
    });
    const expectedResult = [['ipv4', 'BETWEEN', ['123', 'def']]];
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: '123', fieldIndex: 10 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.criteriaCache, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, ipv4 edit second input', function(assert) {
    const initialState2 = Immutable.from({
      criteriaCache: [
        ['ipv4', 'BETWEEN', ['abc', 'def']]
      ],
      group: {
        name: 'test',
        groupCriteria: {
          criteria: [
            ['ipv4', 'BETWEEN', ['abc', 'def']]
          ]
        }
      }
    });
    const expectedResult = [['ipv4', 'BETWEEN', ['abc', '123']]];
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: '123', fieldIndex: 11 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.criteriaCache, expectedResult);
  });


  test('on UPDATE_GROUP_CRITERIA, find if clear ipv4 input on operator change', function(assert) {
    const initialState2 = Immutable.from({
      criteriaCache: [
        ['ipv4', 'BETWEEN', ['abc', 'def']]
      ],
      group: {
        name: 'test',
        groupCriteria: {
          criteria: [
            ['ipv4', 'BETWEEN', ['abc', 'def']]
          ]
        }
      }
    });
    const expectedResult = {
      ...initialState2.group,
      groupCriteria: {
        ...initialState2.group.groupCriteria,
        criteria: [['ipv4', 'IN', []]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: 'IN', fieldIndex: 1 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, find correct ipv6 first operator', function(assert) {
    const expectedResult = {
      ...initialState.group,
      groupCriteria: {
        ...initialState.group.groupCriteria,
        criteria: [['ipv6', 'BETWEEN', []]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: 'ipv6', fieldIndex: 0 }
    };
    const result = reducers(initialState, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on EDIT_GROUP, name, description, etc. are properly set', function(assert) {
    // edit name test
    const nameExpected = 'test name';
    const nameExpectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(['group.name'])
      .build().usm.groupWizard;
    const nameAction = {
      type: ACTION_TYPES.EDIT_GROUP,
      payload: { field: 'group.name', value: nameExpected }
    };
    const nameEndState1 = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), nameAction);
    assert.deepEqual(nameEndState1, nameExpectedEndState, `group name is ${nameExpected}`);
    const nameEndState2 = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), nameAction);
    assert.deepEqual(nameEndState2, nameExpectedEndState, `group name is ${nameExpected} visited state contains no duplicates`);

    // edit description test
    const descExpected = 'test description';
    const descExpectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizDescription(descExpected)
      .groupWizVisited(['group.description'])
      .build().usm.groupWizard;
    const descAction = {
      type: ACTION_TYPES.EDIT_GROUP,
      payload: { field: 'group.description', value: descExpected }
    };
    const descEndState1 = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), descAction);
    assert.deepEqual(descEndState1, descExpectedEndState, `group desc is ${descExpected}`);
    const descEndState2 = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), descAction);
    assert.deepEqual(descEndState2, descExpectedEndState, `group desc is ${descExpected} visited state contains no duplicates`);
  });

  test('on UPDATE_GROUP_STEP step status is properly set - test identifyGroupStep=true', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizStepShowErrors('identifyGroupStep', true)
      .build().usm.groupWizard;
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_STEP,
      payload: { field: 'steps.0.showErrors', value: true }
    };
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'group step status is set correctly');
  });

  test('on UPDATE_GROUP_STEP step status is properly set - test identifyGroupStep=false', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizStepShowErrors('identifyGroupStep', false)
      .build().usm.groupWizard;
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_STEP,
      payload: { field: 'steps.0.showErrors', value: false }
    };
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'group step status is set correctly');
  });

  test('on FETCH_GROUP start, group is reset and itemsStatus is properly set', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupStatus('wait')
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_GROUP });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'group is not-set and groupStatus is wait');
  });

  test('on FETCH_GROUP success, group & itemsStatus are properly set', function(assert) {
    const fetchGroupPayload = {
      data: {
        'id': 'group_001',
        'name': 'Zebra 001',
        'description': 'Zebra 001 of group group_001',
        'dirty': false,
        groupCriteria: {
          conjunction: 'AND',
          criteria: [
            ['osType', 'IN', []]
          ]
        }
      }
    };

    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(fetchGroupPayload.data)
      .groupWizGroupStatus('complete')
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_GROUP,
      payload: fetchGroupPayload
    });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'group is not-set and groupStatus is complete');
  });

  test('on FETCH_GROUP_LIST start, groupList is reset and groupListStatus is properly set', function(assert) {
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupListStatus('wait')
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_GROUP_LIST });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'groupList is not-set and groupListStatus is wait');
  });

  test('on FETCH_GROUP_LIST success, groupList & groupListStatus are properly set', function(assert) {
    const fetchGroupListPayload = {
      data: [
        {
          id: 'group_001',
          name: 'Zebra 001',
          description: 'Zebra 001 of group group_001',
          createdOn: 1523655354337,
          lastModifiedOn: 1523655354337,
          lastPublishedOn: 1523655354337,
          dirty: false
        },
        {
          id: 'group_002',
          name: 'Awesome! 012',
          description: 'Awesome! 012 of group group_012',
          createdOn: 1523655368173,
          lastModifiedOn: 1523655368173,
          lastPublishedOn: 1523655368173,
          dirty: true
        },
        {
          id: 'group_003',
          name: 'Xylaphone 003',
          description: 'Xylaphone 003 of group group_003',
          createdOn: 1523655354337,
          lastModifiedOn: 1523655354337,
          lastPublishedOn: 0,
          dirty: true
        }
      ]
    };

    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupList(fetchGroupListPayload.data)
      .groupWizGroupListStatus('complete')
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_GROUP_LIST,
      payload: fetchGroupListPayload
    });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, 'groupList is not-set and groupListStatus is complete');
  });

  test('on SAVE_GROUP start, groupStatus is properly set', function(assert) {
    const groupStatusExpected = 'wait';
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupStatus(groupStatusExpected)
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SAVE_GROUP });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, `groupStatus is ${groupStatusExpected}`);
  });

  test('on SAVE_GROUP success, group & groupStatus are properly set', function(assert) {
    const nameExpected = 'test name';
    const descExpected = 'test description';
    const groupStatusExpected = 'complete';
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizDescription(descExpected)
      .groupWizGroupStatus(groupStatusExpected)
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SAVE_GROUP,
      payload: { data: _.cloneDeep(expectedEndState.group) }
    });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, `group populated & groupStatus is ${groupStatusExpected}`);
  });

  test('on SAVE_PUBLISH_GROUP start, groupStatus is properly set', function(assert) {
    const groupStatusExpected = 'wait';
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupStatus(groupStatusExpected)
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SAVE_PUBLISH_GROUP });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, `groupStatus is ${groupStatusExpected}`);
  });

  test('on SAVE_PUBLISH_GROUP success, group & groupStatus are properly set', function(assert) {
    const nameExpected = 'test name';
    const descExpected = 'test description';
    const groupStatusExpected = 'complete';
    const expectedEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizDescription(descExpected)
      .groupWizGroupStatus(groupStatusExpected)
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SAVE_PUBLISH_GROUP,
      payload: { data: _.cloneDeep(expectedEndState.group) }
    });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, expectedEndState, `group populated & groupStatus is ${groupStatusExpected}`);
  });

});

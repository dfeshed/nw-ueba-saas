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

module('Unit | Reducers | group Wizard Reducers', function() {

  test('should return the initial state', function(assert) {
    const endState = reducers(undefined, {});
    assert.deepEqual(endState, groupWizInitialState);
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
        criteria: [['osDescription', 'EQUALS', []]]
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
        criteria: [['hostname', 'EQUALS', []]]
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
      group: {
        name: 'test',
        groupCriteria: {
          criteria: [
            ['ipv4', 'BETWEEN', []]
          ]
        }
      }
    });
    const expectedResult = {
      ...initialState2.group,
      groupCriteria: {
        ...initialState2.group.groupCriteria,
        criteria: [['ipv4', 'BETWEEN', ['123']]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: '123', fieldIndex: 10 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, ipv4 BETWEEN add to second input', function(assert) {
    const initialState2 = Immutable.from({
      group: {
        name: 'test',
        groupCriteria: {
          criteria: [
            ['ipv4', 'BETWEEN', ['123']]
          ]
        }
      }
    });
    const expectedResult = {
      ...initialState2.group,
      groupCriteria: {
        ...initialState2.group.groupCriteria,
        criteria: [['ipv4', 'BETWEEN', ['123', 'def']]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: 'def', fieldIndex: 11 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, ipv4 BETWEEN add to second input with empty first input', function(assert) {
    const initialState2 = Immutable.from({
      group: {
        name: 'test',
        groupCriteria: {
          criteria: [
            ['ipv4', 'BETWEEN', []]
          ]
        }
      }
    });
    const expectedResult = {
      ...initialState2.group,
      groupCriteria: {
        ...initialState2.group.groupCriteria,
        criteria: [['ipv4', 'BETWEEN', [undefined, 'def']]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: 'def', fieldIndex: 11 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, ipv4 edit first input', function(assert) {
    const initialState2 = Immutable.from({
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
        criteria: [['ipv4', 'BETWEEN', ['123', 'def']]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: '123', fieldIndex: 10 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.group, expectedResult);
  });

  test('on UPDATE_GROUP_CRITERIA, ipv4 edit second input', function(assert) {
    const initialState2 = Immutable.from({
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
        criteria: [['ipv4', 'BETWEEN', ['abc', '123']]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: '123', fieldIndex: 11 }
    };
    const result = reducers(initialState2, action);
    assert.deepEqual(result.group, expectedResult);
  });


  test('on UPDATE_GROUP_CRITERIA, find if clear ipv4 input on operator change', function(assert) {
    const initialState2 = Immutable.from({
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

  test('on UPDATE_GROUP_CRITERIA, find correct agentMode first operator', function(assert) {
    const expectedResult = {
      ...initialState.group,
      groupCriteria: {
        ...initialState.group.groupCriteria,
        criteria: [['agentMode', 'EQUALS', []]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: 'agentMode', fieldIndex: 0 }
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

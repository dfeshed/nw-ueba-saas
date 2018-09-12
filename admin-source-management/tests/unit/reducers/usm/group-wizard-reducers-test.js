import Immutable from 'seamless-immutable';
import _ from 'lodash';
import { module, test } from 'qunit';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
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

  test('on UPDATE_GROUP_CRITERIA, find correct osType operator', function(assert) {
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

  test('on UPDATE_GROUP_CRITERIA, find correct osDescription operator', function(assert) {
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

  test('on UPDATE_GROUP_CRITERIA, find correct hostname operator', function(assert) {
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

  test('on UPDATE_GROUP_CRITERIA, find correct ipv4 operator', function(assert) {
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

  test('on UPDATE_GROUP_CRITERIA, find correct ipv4 second input', function(assert) {
    const initialState2 = Immutable.from({
      group: {
        name: 'test',
        groupCriteria: {
          criteria: [
            ['ipv4', 'BETWEEN', ['abc']]
          ]
        }
      }
    });
    const expectedResult = {
      ...initialState2.group,
      groupCriteria: {
        ...initialState2.group.groupCriteria,
        criteria: [['ipv4', 'BETWEEN', ['abc', 'def']]]
      }
    };
    const action = {
      type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
      payload: { criteriaPath: ',0', value: ['def'], fieldIndex: 3 }
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

  test('on UPDATE_GROUP_CRITERIA, find correct ipv6 operator', function(assert) {
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

  test('on UPDATE_GROUP_CRITERIA, find correct agentMode operator', function(assert) {
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

  test('on GET_GROUP start, group is reset and itemsStatus is properly set', function(assert) {
    const getGroupEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupStatus('wait')
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_GROUP });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, getGroupEndState, 'group is not-set and groupStatus is wait');
  });

  test('on GET_GROUP success, group & itemsStatus are properly set', function(assert) {
    const getGroupPayload = {
      data: [
        {
          'id': 'group_001',
          'name': 'Zebra 001',
          'description': 'Zebra 001 of group group_001',
          'dirty': false
        }
      ]
    };

    const getGroupEndState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(getGroupPayload.data)
      .groupWizGroupStatus('complete')
      .build().usm.groupWizard;
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_GROUP,
      payload: getGroupPayload
    });
    const endState = reducers(Immutable.from(_.cloneDeep(groupWizInitialState)), action);
    assert.deepEqual(endState, getGroupEndState, 'group is not-set and groupStatus is complete');
  });

});

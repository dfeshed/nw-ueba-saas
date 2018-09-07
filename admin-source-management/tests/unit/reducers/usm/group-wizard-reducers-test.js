import Immutable from 'seamless-immutable';
import _ from 'lodash';
import { module, test } from 'qunit';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/group-wizard-reducers';

const groupWizInitialState = new ReduxDataHelper().groupWiz().build().usm.groupWizard;

module('Unit | Reducers | group Wizard Reducers', function() {

  test('should return the initial state', function(assert) {
    const endState = reducers(undefined, {});
    assert.deepEqual(endState, groupWizInitialState);
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

});

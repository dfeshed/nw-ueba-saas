import Immutable from 'seamless-immutable';
import _ from 'lodash';
import { module, test } from 'qunit';
// import { LIFECYCLE } from 'redux-pack';
// import makePackAction from '../../../helpers/make-pack-action';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import reducers from 'admin-source-management/reducers/usm/policy-wizard-reducers';

const policyWizInitialState = new ReduxDataHelper().policyWiz().build().usm.policyWizard;

module('Unit | Reducers | Policy Wizard Reducers', function() {

  test('should return the initial state', function(assert) {
    const endState = reducers(undefined, {});
    assert.deepEqual(endState, policyWizInitialState);
  });

  test('on EDIT_POLICY, name, description, etc. are properly set', function(assert) {
    // edit name test
    const nameExpected = 'test name';
    const nameExpectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizVisited(['policy.name'])
      .build().usm.policyWizard;
    const nameAction = {
      type: ACTION_TYPES.EDIT_POLICY,
      payload: { field: 'policy.name', value: nameExpected }
    };
    const nameEndState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), nameAction);
    assert.deepEqual(nameEndState, nameExpectedEndState, `policy name is ${nameExpected}`);

    // edit description test
    const descExpected = 'test description';
    const descExpectedEndState = new ReduxDataHelper()
      .policyWiz()
      .policyWizDescription(descExpected)
      .policyWizVisited(['policy.description'])
      .build().usm.policyWizard;
    const descAction = {
      type: ACTION_TYPES.EDIT_POLICY,
      payload: { field: 'policy.description', value: descExpected }
    };
    const descEndState = reducers(Immutable.from(_.cloneDeep(policyWizInitialState)), descAction);
    assert.deepEqual(descEndState, descExpectedEndState, `policy desc is ${descExpected}`);
  });

});

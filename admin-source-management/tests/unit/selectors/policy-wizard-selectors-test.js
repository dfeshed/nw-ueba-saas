import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import {
  policy,
  visited,
  sourceTypes,
  selectedSourceType,
  nameValidator,
  steps,
  isIdentifyPolicyStepValid
  // TODO when implemented isDefinePolicyStepvalid,
  // TODO when implemented isApplyToGroupStepvalid,
  // TODO when implemented isReviewPolicyStepvalid,
  // TODO when implemented isWizardValid
} from 'admin-source-management/reducers/usm/policy-wizard-selectors';

module('Unit | Selectors | Policy Wizard Selectors', function() {

  test('policy selector', function(assert) {
    const nameExpected = 'test name';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .build();
    const policyExpected = _.cloneDeep(fullState.usm.policyWizard.policy);
    const policySelected = policy(Immutable.from(fullState));
    assert.deepEqual(policySelected, policyExpected, 'The returned value from the policy selector is as expected');
    assert.deepEqual(policySelected.name, nameExpected, `policy name is ${nameExpected}`);
  });

  test('visited selector', function(assert) {
    const visitedExpected = ['policy.name', 'policy.description'];
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizVisited(visitedExpected)
      .build();
    const visitedSelected = visited(Immutable.from(fullState));
    assert.deepEqual(visitedSelected, visitedExpected, 'The returned value from the visited selector is as expected');
  });

  test('sourceTypes selector', function(assert) {
    const type0Expected = 'edrPolicy';
    const fullState = new ReduxDataHelper().policyWiz().build();
    const sourceTypesExpected = _.cloneDeep(fullState.usm.policyWizard.sourceTypes);
    const sourceTypesSelected = sourceTypes(Immutable.from(fullState));
    assert.deepEqual(sourceTypesSelected, sourceTypesExpected, 'The returned value from the sourceTypes selector is as expected');
    assert.deepEqual(sourceTypesSelected[0].type, type0Expected, `sourceTypes[0].type is ${type0Expected}`);
  });

  test('selectedSourceType selector', function(assert) {
    const typeExpected = 'edrPolicy';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizSourceType(typeExpected) // type holds sourceType type only so use the first type
      .build();
    // the selector looks up sourceType object by type, so use the first object
    const sourceTypeExpected = _.cloneDeep(fullState.usm.policyWizard.sourceTypes[0]);
    const sourceTypeSelected = selectedSourceType(Immutable.from(fullState));
    assert.deepEqual(sourceTypeSelected, sourceTypeExpected, 'The returned value from the selectedSourceType selector is as expected');
  });

  test('nameValidator selector', function(assert) {
    // error & not visited
    let nameExpected = '';
    let visitedExpected = [];
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizVisited(visitedExpected)
      .build();
    let nameValidatorExpected = {
      isError: true,
      errorMessage: 'adminUsm.policyWizard.nameRequired',
      isVisited: false
    };
    let nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The returned value from the nameValidator selector is as expected');

    // error & visited
    nameExpected = '';
    visitedExpected = ['policy.name'];
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizVisited(visitedExpected)
      .build();
    nameValidatorExpected = {
      isError: true,
      errorMessage: 'adminUsm.policyWizard.nameRequired',
      isVisited: true
    };
    nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The returned value from the nameValidator selector is as expected');

    // no error & visited
    nameExpected = 'test name';
    visitedExpected = ['policy.name'];
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizVisited(visitedExpected)
      .build();
    nameValidatorExpected = {
      isError: false,
      errorMessage: 'adminUsm.policyWizard.nameRequired',
      isVisited: true
    };
    nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The returned value from the nameValidator selector is as expected');
  });

  test('steps selector', function(assert) {
    const stepId0Expected = 'identifyPolicyStep';
    const fullState = new ReduxDataHelper().policyWiz().build();
    const stepsExpected = _.cloneDeep(fullState.usm.policyWizard.steps);
    const stepsSelected = steps(Immutable.from(fullState));
    assert.deepEqual(stepsSelected, stepsExpected, 'The returned value from the steps selector is as expected');
    assert.deepEqual(stepsSelected[0].id, stepId0Expected, `steps[0].id is ${stepId0Expected}`);
  });

  test('isIdentifyPolicyStepValid selector', function(assert) {
    // invalid
    let nameExpected = '';
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .build();
    let isIdentifyPolicyStepValidExpected = false;
    let isIdentifyPolicyStepValidSelected = isIdentifyPolicyStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyPolicyStepValidSelected, isIdentifyPolicyStepValidExpected, 'The returned value from the isIdentifyPolicyStepValid selector is as expected');

    // valid
    nameExpected = 'test name';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .build();
    isIdentifyPolicyStepValidExpected = true;
    isIdentifyPolicyStepValidSelected = isIdentifyPolicyStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyPolicyStepValidSelected, isIdentifyPolicyStepValidExpected, 'The returned value from the isIdentifyPolicyStepValid selector is as expected');
  });

});

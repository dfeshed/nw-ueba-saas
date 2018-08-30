import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import {
  group,
  visited,
  nameValidator,
  steps,
  isIdentifyGroupStepValid
  // TODO when implemented isDefineGroupStepvalid,
  // TODO when implemented isApplyPolicyStepvalid,
  // TODO when implemented isReviewGroupStepvalid,
  // TODO when implemented isWizardValid
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

module('Unit | Selectors | Group Wizard Selectors', function() {

  test('group selector', function(assert) {
    const nameExpected = 'test name';
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .build();
    const groupExpected = _.cloneDeep(fullState.usm.groupWizard.group);
    const groupSelected = group(Immutable.from(fullState));
    assert.deepEqual(groupSelected, groupExpected, 'The returned value from the group selector is as expected');
    assert.deepEqual(groupSelected.name, nameExpected, `group name is ${nameExpected}`);
  });

  test('visited selector', function(assert) {
    const visitedExpected = ['group.name', 'group.description'];
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizVisited(visitedExpected)
      .build();
    const visitedSelected = visited(Immutable.from(fullState));
    assert.deepEqual(visitedSelected, visitedExpected, 'The returned value from the visited selector is as expected');
  });

  test('nameValidator selector', function(assert) {
    // error & not visited
    let nameExpected = '';
    let visitedExpected = [];
    let fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .build();
    let nameValidatorExpected = {
      isError: true,
      errorMessage: 'adminUsm.groupWizard.nameRequired',
      isVisited: false
    };
    let nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The returned value from the nameValidator selector is as expected');

    // error & visited
    nameExpected = '';
    visitedExpected = ['group.name'];
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .build();
    nameValidatorExpected = {
      isError: true,
      errorMessage: 'adminUsm.groupWizard.nameRequired',
      isVisited: true
    };
    nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The returned value from the nameValidator selector is as expected');

    // no error & visited
    nameExpected = 'test name';
    visitedExpected = ['group.name'];
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .build();
    nameValidatorExpected = {
      isError: false,
      errorMessage: 'adminUsm.groupWizard.nameRequired',
      isVisited: true
    };
    nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The returned value from the nameValidator selector is as expected');
  });

  test('steps selector', function(assert) {
    const stepId0Expected = 'identifyGroupStep';
    const fullState = new ReduxDataHelper().groupWiz().build();
    const stepsExpected = _.cloneDeep(fullState.usm.groupWizard.steps);
    const stepsSelected = steps(Immutable.from(fullState));
    assert.deepEqual(stepsSelected, stepsExpected, 'The returned value from the steps selector is as expected');
    assert.deepEqual(stepsSelected[0].id, stepId0Expected, `steps[0].id is ${stepId0Expected}`);
  });

  test('isIdentifyGroupStepValid selector', function(assert) {
    // invalid
    let nameExpected = '';
    let fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .build();
    let isIdentifyGroupStepValidExpected = false;
    let isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');

    // valid
    nameExpected = 'test name';
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .build();
    isIdentifyGroupStepValidExpected = true;
    isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');
  });

});

import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import {
  group,
  visited,
  nameValidator,
  descriptionValidator,
  steps,
  isIdentifyGroupStepValid,
  // TODO when implemented isDefineGroupStepvalid,
  // TODO when implemented isApplyPolicyStepvalid,
  // TODO when implemented isReviewGroupStepvalid,
  // TODO when implemented isWizardValid
  isGroupLoading
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
    // isBlank & not visited
    let nameExpected = '';
    let visitedExpected = [];
    let fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .build();
    let nameValidatorExpected = {
      isError: true,
      showError: false,
      errorMessage: ''
    };
    let nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (isBlank & not visited) returned value from the nameValidator selector is as expected');

    // isBlank & visited
    nameExpected = '';
    visitedExpected = ['group.name'];
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .build();
    nameValidatorExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.groupWizard.nameRequired'
    };
    nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (isBlank & visited) returned value from the nameValidator selector is as expected');

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
      showError: false,
      errorMessage: ''
    };
    nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (no error & visited) returned value from the nameValidator selector is as expected');

    // exceedsLength
    for (let index = 0; index < 10; index++) {
      nameExpected += 'the-name-is-greater-than-256-';
    }
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .build();
    nameValidatorExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.groupWizard.nameExceedsMaxLength'
    };
    nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (nameExceedsMaxLength) returned value from the nameValidator selector is as expected');
  });

  test('descriptionValidator selector', function(assert) {
    // isBlank & not visited
    let descExpected = '';
    let visitedExpected = [];
    let fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizDescription(descExpected)
      .groupWizVisited(visitedExpected)
      .build();
    let descriptionValidatorExpected = {
      isError: false,
      showError: false,
      errorMessage: ''
    };
    let descValidatorSelected = descriptionValidator(Immutable.from(fullState));
    assert.deepEqual(descValidatorSelected, descriptionValidatorExpected, 'The (isBlank & not visited) returned value from the descriptionValidator selector is as expected');

    // isBlank & visited
    descExpected = '';
    visitedExpected = ['group.description'];
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizDescription(descExpected)
      .groupWizVisited(visitedExpected)
      .build();
    descriptionValidatorExpected = {
      isError: false,
      showError: false,
      errorMessage: ''
    };
    descValidatorSelected = descriptionValidator(Immutable.from(fullState));
    assert.deepEqual(descValidatorSelected, descriptionValidatorExpected, 'The (isBlank & visited) returned value from the descriptionValidator selector is as expected');

    // no error & visited
    descExpected = 'test description';
    visitedExpected = ['group.description'];
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizDescription(descExpected)
      .groupWizVisited(visitedExpected)
      .build();
    descriptionValidatorExpected = {
      isError: false,
      showError: false,
      errorMessage: ''
    };
    descValidatorSelected = descriptionValidator(Immutable.from(fullState));
    assert.deepEqual(descValidatorSelected, descriptionValidatorExpected, 'The (no error & visited) returned value from the descriptionValidator selector is as expected');

    // descriptionExceedsMaxLength & visited
    for (let index = 0; index < 220; index++) {
      descExpected += 'the-description-is-greater-than-8000-';
    }
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizDescription(descExpected)
      .build();
    descriptionValidatorExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.groupWizard.descriptionExceedsMaxLength'
    };
    descValidatorSelected = descriptionValidator(Immutable.from(fullState));
    assert.deepEqual(descValidatorSelected, descriptionValidatorExpected, 'The (descriptionExceedsMaxLength) returned value from the descriptionValidator selector is as expected');
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
    let visitedExpected = ['group.name'];
    let fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .build();
    let isIdentifyGroupStepValidExpected = false;
    let isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');

    // valid
    nameExpected = 'test name';
    visitedExpected = ['group.name'];
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .build();
    isIdentifyGroupStepValidExpected = true;
    isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');
  });

  test('isGroupLoading selector', function(assert) {
    let groupStatusExpected = 'wait';
    let fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupStatus(groupStatusExpected)
      .build();
    let isGroupLoadingExpected = true;
    let isGroupLoadingSelected = isGroupLoading(Immutable.from(fullState));
    assert.deepEqual(isGroupLoadingSelected, isGroupLoadingExpected, 'isGroupLoading is true when groupStatus is wait');

    groupStatusExpected = 'complete';
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupStatus(groupStatusExpected)
      .build();
    isGroupLoadingExpected = false;
    isGroupLoadingSelected = isGroupLoading(Immutable.from(fullState));
    assert.deepEqual(isGroupLoadingSelected, isGroupLoadingExpected, 'isGroupLoading is false when groupStatus is complete');
  });

});

import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { lookup } from 'ember-dependency-lookup';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import {
  group,
  visited,
  nameValidator,
  descriptionValidator,
  steps,
  isGroupCriteriaEmpty,
  isIdentifyGroupStepValid,
  identifyGroupStepShowErrors,
  isDefineGroupStepValid,
  defineGroupStepShowErrors,
  isApplyPolicyStepValid,
  applyPolicyStepShowErrors,
  // TODO when implemented isReviewGroupStepValid,
  // TODO when implemented isWizardValid
  isGroupLoading,
  isGroupFetchError,
  hasGroupChanged,
  policyList,
  availablePolicySourceTypes,
  enabledPolicySourceTypesAsObjs,
  selectedSourceTypeAsObj,
  assignedPolicyList,
  groupCriteriaValidator,
  policyAssignmentValidator,
  limitedPolicySourceTypes,
  groupRankingQuery,
  groupRankingViewQuery,
  isLoadingGroupRanking,
  groupRankingSelectedIndex

} from 'admin-source-management/reducers/usm/group-wizard-selectors';

module('Unit | Selectors | Group Wizard Selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

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

  test('nameValidator selector - isBlank & not visited', function(assert) {
    const nameExpected = '';
    const visitedExpected = [];
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .build();
    const nameValidatorExpected = {
      isError: true,
      showError: false,
      errorMessage: ''
    };
    const nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (isBlank & not visited) returned value from the nameValidator selector is as expected');
    const isIdentifyGroupStepValidExpected = false;
    const isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');
  });

  test('nameValidator selector - isBlank & visited', function(assert) {
    const nameExpected = '';
    const visitedExpected = ['group.name'];
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .build();
    const nameValidatorExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.groupWizard.nameRequired'
    };
    const nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (isBlank & visited) returned value from the nameValidator selector is as expected');
    const isIdentifyGroupStepValidExpected = false;
    const isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');
  });

  test('nameValidator selector - nameExists', function(assert) {
    const nameExpected = 'existingName';
    const visitedExpected = ['group.name'];
    const groupListPayload = [
      {
        id: 'group_000',
        name: 'existingName',
        description: '',
        createdOn: 1523655354337,
        lastModifiedOn: 1523655354337,
        lastPublishedOn: 1523655354337,
        dirty: false
      }
    ];

    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .groupWizGroupList(groupListPayload)
      .build();
    const nameValidatorExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.groupWizard.nameExists'
    };
    const nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (nameExists) returned value from the nameValidator selector is as expected');
    const isIdentifyGroupStepValidExpected = false;
    const isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');
  });

  test('nameValidator selector - nameExists for self check', function(assert) {
    const nameExpected = 'existingName';
    const idExpected = 'group_000';
    const visitedExpected = ['group.name'];
    const groupListPayload2 = [
      {
        id: 'group_000',
        name: 'existingName',
        description: '',
        createdOn: 1523655354337,
        lastModifiedOn: 1523655354337,
        lastPublishedOn: 1523655354337,
        dirty: false
      }
    ];

    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizId(idExpected)
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .groupWizGroupList(groupListPayload2)
      .build();
    const nameValidatorExpected = {
      isError: false,
      showError: false,
      errorMessage: ''
    };
    const nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (nameExists) returned value from the nameValidator selector is as expected');
    const isIdentifyGroupStepValidExpected = true;
    const isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');

  });

  test('nameValidator selector - no error & visited', function(assert) {
    const nameExpected = 'test name';
    const visitedExpected = ['group.name'];
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizVisited(visitedExpected)
      .build();
    const nameValidatorExpected = {
      isError: false,
      showError: false,
      errorMessage: ''
    };
    const nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (no error & visited) returned value from the nameValidator selector is as expected');
    const isIdentifyGroupStepValidExpected = true;
    const isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');

  });

  test('nameValidator selector - exceedsLength', function(assert) {
    let nameExpected = 'test name';
    for (let index = 0; index < 10; index++) {
      nameExpected += 'the-name-is-greater-than-256-';
    }
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .build();
    const nameValidatorExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.groupWizard.nameExceedsMaxLength'
    };
    const nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (nameExceedsMaxLength) returned value from the nameValidator selector is as expected');
    const isIdentifyGroupStepValidExpected = false;
    const isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');
  });

  test('descriptionValidator selector - isBlank & not visited', function(assert) {
    const nameExpected = 'test name';
    const descExpected = '';
    const visitedExpected = [];
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizDescription(descExpected)
      .groupWizVisited(visitedExpected)
      .build();
    const descriptionValidatorExpected = {
      isError: false,
      showError: false,
      errorMessage: ''
    };
    const descValidatorSelected = descriptionValidator(Immutable.from(fullState));
    assert.deepEqual(descValidatorSelected, descriptionValidatorExpected, 'The (isBlank & not visited) returned value from the descriptionValidator selector is as expected');
    const isIdentifyGroupStepValidExpected = true;
    const isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');
  });

  test('descriptionValidator selector - isBlank & visited', function(assert) {
    const nameExpected = 'test name';
    const descExpected = '';
    const visitedExpected = ['group.description'];
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizDescription(descExpected)
      .groupWizVisited(visitedExpected)
      .build();
    const descriptionValidatorExpected = {
      isError: false,
      showError: false,
      errorMessage: ''
    };
    const descValidatorSelected = descriptionValidator(Immutable.from(fullState));
    assert.deepEqual(descValidatorSelected, descriptionValidatorExpected, 'The (isBlank & visited) returned value from the descriptionValidator selector is as expected');
    const isIdentifyGroupStepValidExpected = true;
    const isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');
  });

  test('descriptionValidator selector - no error & visited', function(assert) {
    const nameExpected = 'test name';
    const descExpected = 'test description';
    const visitedExpected = ['group.description'];
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizDescription(descExpected)
      .groupWizVisited(visitedExpected)
      .build();
    const descriptionValidatorExpected = {
      isError: false,
      showError: false,
      errorMessage: ''
    };
    const descValidatorSelected = descriptionValidator(Immutable.from(fullState));
    assert.deepEqual(descValidatorSelected, descriptionValidatorExpected, 'The (no error & visited) returned value from the descriptionValidator selector is as expected');
    const isIdentifyGroupStepValidExpected = true;
    const isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyGroupStepValidSelected, isIdentifyGroupStepValidExpected, 'The returned value from the isIdentifyGroupStepValid selector is as expected');
  });

  test('descriptionValidator selector - exceedsMaxLength & visited', function(assert) {
    const nameExpected = 'test name';
    let descExpected = '';
    for (let index = 0; index < 220; index++) {
      descExpected += 'the-description-is-greater-than-8000-';
    }
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizName(nameExpected)
      .groupWizDescription(descExpected)
      .build();
    const descriptionValidatorExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.groupWizard.descriptionExceedsMaxLength'
    };
    const descValidatorSelected = descriptionValidator(Immutable.from(fullState));
    assert.deepEqual(descValidatorSelected, descriptionValidatorExpected, 'The (descriptionExceedsMaxLength) returned value from the descriptionValidator selector is as expected');
    const isIdentifyGroupStepValidExpected = false;
    const isIdentifyGroupStepValidSelected = isIdentifyGroupStepValid(Immutable.from(fullState));
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

    // should also be true when groupFetchStatus is true
    groupStatusExpected = 'wait';
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupFetchStatus(groupStatusExpected)
      .build();
    isGroupLoadingExpected = true;
    isGroupLoadingSelected = isGroupLoading(Immutable.from(fullState));
    assert.deepEqual(isGroupLoadingSelected, isGroupLoadingExpected, 'isGroupLoading is true when groupFetchStatus is wait');

    groupStatusExpected = 'complete';
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupStatus(groupStatusExpected)
      .build();
    isGroupLoadingExpected = false;
    isGroupLoadingSelected = isGroupLoading(Immutable.from(fullState));
    assert.deepEqual(isGroupLoadingSelected, isGroupLoadingExpected, 'isGroupLoading is false when groupStatus is complete');
  });

  test('isGroupFetchError selector', function(assert) {
    let groupStatusExpected = 'complete';
    let fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupFetchStatus(groupStatusExpected)
      .build();
    let isGroupFetchErrorExpected = false;
    let isGroupFetchErrorSelected = isGroupFetchError(Immutable.from(fullState));
    assert.deepEqual(isGroupFetchErrorSelected, isGroupFetchErrorExpected, 'isGroupFetchError is false when groupFetchStatus is complete');

    groupStatusExpected = 'error';
    fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroupFetchStatus(groupStatusExpected)
      .build();
    isGroupFetchErrorExpected = true;
    isGroupFetchErrorSelected = isGroupFetchError(Immutable.from(fullState));
    assert.deepEqual(isGroupFetchErrorSelected, isGroupFetchErrorExpected, 'isGroupFetchError is true when groupFetchStatus is error');
  });

  test('hasGroupChanged selector', function(assert) {
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'groupCriteria': {
        'conjunction': 'AND',
        'criteria': [
          ['osType', 'IN', []]
        ]
      },
      'assignedPolicies': {}
    };
    const nochangeState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload, true)
      .build();
    assert.equal(hasGroupChanged(Immutable.from(nochangeState)), false, 'The returned value from the hasGroupChanged selector is as false when no change has been made');

    const changeState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload, false)
      .build();
    assert.equal(hasGroupChanged(Immutable.from(changeState)), true, 'The returned value from the hasGroupChanged selector is as true when change has been made');
  });

  test('steps selector', function(assert) {
    const stepId0Expected = 'identifyGroupStep';
    const fullState = new ReduxDataHelper().groupWiz().build();
    const stepsExpected = _.cloneDeep(fullState.usm.groupWizard.steps);
    const stepsSelected = steps(Immutable.from(fullState));
    assert.deepEqual(stepsSelected, stepsExpected, 'The returned value from the steps selector is as expected');
    assert.deepEqual(stepsSelected[0].id, stepId0Expected, `steps[0].id is ${stepId0Expected}`);
  });

  test('identifyGroupStepShowErrors selector - false', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizStepShowErrors('identifyGroupStep', false)
      .build();
    assert.equal(identifyGroupStepShowErrors(Immutable.from(fullState)), false, 'identifyGroupStepShowErrors is true when next button is visited');
  });

  test('identifyGroupStepShowErrors selector - true', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizStepShowErrors('identifyGroupStep', true)
      .build();
    assert.equal(identifyGroupStepShowErrors(Immutable.from(fullState)), true, 'identifyGroupStepShowErrors is true when next button is visited');
  });

  test('defineGroupStepShowErrors selector - false', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizStepShowErrors('defineGroupStep', false)
      .build();
    assert.equal(defineGroupStepShowErrors(Immutable.from(fullState)), false, 'identifyGroupStepShowErrors is true when next button is visited');
  });

  test('defineGroupStepShowErrors selector - true', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizStepShowErrors('defineGroupStep', true)
      .build();
    assert.equal(defineGroupStepShowErrors(Immutable.from(fullState)), true, 'identifyGroupStepShowErrors is true when next button is visited');
  });

  test('applyPolicyStepShowErrors selector - false', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizStepShowErrors('applyPolicyStep', false)
      .build();
    assert.equal(applyPolicyStepShowErrors(Immutable.from(fullState)), false, 'identifyGroupStepShowErrors is true when next button is visited');
  });

  test('applyPolicyStepShowErrors selector - true', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizStepShowErrors('applyPolicyStep', true)
      .build();
    assert.equal(applyPolicyStepShowErrors(Immutable.from(fullState)), true, 'identifyGroupStepShowErrors is true when next button is visited');
  });

  const policyListPayload = [
    {
      id: 'policy_003',
      name: 'Policy 003',
      policyType: 'windowsLogPolicy',
      description: 'EMC Reston 012 of policy policy_012',
      lastPublishedOn: 0,
      dirty: true,
      defaultPolicy: false
    },
    {
      id: '__default_edr_policy',
      name: 'Default EDR Policy',
      policyType: 'edrPolicy',
      description: 'Default EDR Policy __default_edr_policy',
      lastPublishedOn: 1527489158739,
      dirty: false,
      defaultPolicy: true
    },
    {
      id: 'policy_001',
      name: 'Policy 001',
      policyType: 'edrPolicy',
      description: 'EMC 001 of policy policy_001',
      lastPublishedOn: 1527489158739,
      dirty: true,
      defaultPolicy: false
    },
    {
      id: 'policy_002',
      name: 'Policy 002',
      policyType: 'edrPolicy',
      description: 'EMC Reston 012 of policy policy_012',
      lastPublishedOn: 0,
      dirty: true,
      defaultPolicy: false
    }
  ];

  const groupPayload1 = {
    'id': 'group_001',
    'name': 'Group 001',
    'assignedPolicies': {
      'edrPolicy': {
        'referenceId': 'policy_001',
        'name': 'Policy 001'
      }
    }
  };

  const groupPayload2 = {
    'id': 'group_002',
    'name': 'Group 002',
    'assignedPolicies': {
      'edrPolicy': {
        'referenceId': 'policy_001',
        'name': 'EMC 001'
      },
      'windowsLogPolicy': {
        'referenceId': 'policy_003',
        'name': 'Policy 003'
      }
    }
  };

  test('policyList selector', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload1)
      .groupWizPolicyList(policyListPayload)
      .build();
    const policyListSelected = policyList(Immutable.from(fullState));
    assert.deepEqual(policyListSelected, policyListPayload, 'The returned value from the policies selector is as expected');
  });

  test('availablePolicySourceTypes selector', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload1)
      .groupWizPolicyList(policyListPayload)
      .build();
    const sourceTypes = availablePolicySourceTypes(Immutable.from(fullState));
    assert.deepEqual(sourceTypes, ['edrPolicy', 'windowsLogPolicy'], 'The returned value from the availablePolicySourceTypes is as expected');
  });

  test('enabledPolicySourceTypesAsObjs selector', function(assert) {
    const features = lookup('service:features');
    const availableSourceTypes = ['edrPolicy', 'windowsLogPolicy', 'filePolicy'];

    // allowFilePolicies enabled so filePolicy type should be enabled
    features.setFeatureFlags({ 'rsa.usm.allowFilePolicies': true });
    let expectedSourceTypesAsObjs = [
      { policyType: 'edrPolicy', disabled: false, label: 'adminUsm.policyTypes.edrPolicy', disabledTooltip: 'adminUsm.policyTypes.edrPolicyDisabledTooltip' },
      { policyType: 'windowsLogPolicy', disabled: false, label: 'adminUsm.policyTypes.windowsLogPolicy', disabledTooltip: 'adminUsm.policyTypes.windowsLogPolicyDisabledTooltip' },
      { policyType: 'filePolicy', disabled: false, label: 'adminUsm.policyTypes.filePolicy', disabledTooltip: 'adminUsm.policyTypes.filePolicyDisabledTooltip' }
    ];
    let actualSourceTypesAsObjs = enabledPolicySourceTypesAsObjs(availableSourceTypes);
    assert.deepEqual(actualSourceTypesAsObjs, expectedSourceTypesAsObjs, 'The returned value from the enabledPolicySourceTypesAsObjs is as expected');

    // allowFilePolicies disabled so filePolicy type should be disabled
    features.setFeatureFlags({ 'rsa.usm.allowFilePolicies': false });
    expectedSourceTypesAsObjs = [
      { policyType: 'edrPolicy', disabled: false, label: 'adminUsm.policyTypes.edrPolicy', disabledTooltip: 'adminUsm.policyTypes.edrPolicyDisabledTooltip' },
      { policyType: 'windowsLogPolicy', disabled: false, label: 'adminUsm.policyTypes.windowsLogPolicy', disabledTooltip: 'adminUsm.policyTypes.windowsLogPolicyDisabledTooltip' },
      { policyType: 'filePolicy', disabled: true, label: 'adminUsm.policyTypes.filePolicy', disabledTooltip: 'adminUsm.policyTypes.filePolicyDisabledTooltip' }
    ];
    actualSourceTypesAsObjs = enabledPolicySourceTypesAsObjs(availableSourceTypes);
    assert.deepEqual(actualSourceTypesAsObjs, expectedSourceTypesAsObjs, 'The returned value from the enabledPolicySourceTypesAsObjs is as expected');
  });

  test('selectedSourceTypeAsObj selector', function(assert) {
    const availableSourceTypesAsObjs = [
      { policyType: 'edrPolicy', disabled: false, label: 'adminUsm.policyTypes.edrPolicy', disabledTooltip: 'adminUsm.policyTypes.edrPolicyDisabledTooltip' },
      { policyType: 'windowsLogPolicy', disabled: false, label: 'adminUsm.policyTypes.windowsLogPolicy', disabledTooltip: 'adminUsm.policyTypes.windowsLogPolicyDisabledTooltip' },
      { policyType: 'filePolicy', disabled: false, label: 'adminUsm.policyTypes.filePolicy', disabledTooltip: 'adminUsm.policyTypes.filePolicyDisabledTooltip' }
    ];
    const [expectedSelectedSourceTypeAsObj] = availableSourceTypesAsObjs;
    const actualSelectedSourceTypeAsObj = selectedSourceTypeAsObj(availableSourceTypesAsObjs, 'edrPolicy');
    assert.deepEqual(actualSelectedSourceTypeAsObj, expectedSelectedSourceTypeAsObj, 'The returned value from the selectedSourceTypeAsObj is as expected');
  });

  test('limitedPolicySourceTypes selector - has edrPolicy policy', function(assert) {
    const expectedPolicyList = ['windowsLogPolicy'];
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload1)
      .groupWizPolicyList(policyListPayload)
      .build();
    const policyList = limitedPolicySourceTypes(Immutable.from(fullState));
    assert.deepEqual(policyList, expectedPolicyList, 'The returned value from limitedPolicySourceTypes is as expected');
  });

  test('limitedPolicySourceTypes selector - has edrPolicy and windowsLogPolicy policies', function(assert) {
    const expectedPolicyList = [];
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload2)
      .groupWizPolicyList(policyListPayload)
      .build();
    const policyList = limitedPolicySourceTypes(Immutable.from(fullState));
    assert.deepEqual(policyList, expectedPolicyList, 'The returned value from limitedPolicySourceTypes is as expected');
  });

  test('limitedPolicySourceTypes selector - has windowsLogPolicy policy', function(assert) {
    const groupPayload3 = {
      'id': 'group_002',
      'name': 'Group 002',
      'assignedPolicies': {
        'windowsLogPolicy': {
          'referenceId': 'policy_003',
          'name': 'Policy 003'
        }
      }
    };
    const expectedPolicyList = ['edrPolicy'];
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload3)
      .groupWizPolicyList(policyListPayload)
      .build();
    const policyList = limitedPolicySourceTypes(Immutable.from(fullState));
    assert.deepEqual(policyList, expectedPolicyList, 'The returned value from limitedPolicySourceTypes is as expected');
  });

  test('assignedPolicyList selector - single assigned source type', function(assert) {
    const expectedPolicyList = [
      {
        id: 'policy_001',
        name: 'Policy 001',
        policyType: 'edrPolicy',
        description: 'EMC 001 of policy policy_001',
        lastPublishedOn: 1527489158739,
        dirty: true,
        defaultPolicy: false
      }
    ];

    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload1)
      .groupWizPolicyList(policyListPayload)
      .build();
    const policyList = assignedPolicyList(Immutable.from(fullState));
    assert.deepEqual(policyList, expectedPolicyList, 'The returned value from the assignedPolicyList is as expected');
  });

  test('assignedPolicyList selector - multiple assigned source type', function(assert) {
    const expectedPolicyList = [
      {
        id: 'policy_001',
        name: 'Policy 001',
        policyType: 'edrPolicy',
        description: 'EMC 001 of policy policy_001',
        lastPublishedOn: 1527489158739,
        dirty: true,
        defaultPolicy: false
      },
      {
        id: 'policy_003',
        name: 'Policy 003',
        policyType: 'windowsLogPolicy',
        description: 'EMC Reston 012 of policy policy_012',
        lastPublishedOn: 0,
        dirty: true,
        defaultPolicy: false
      }
    ];

    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload2)
      .groupWizPolicyList(policyListPayload)
      .build();
    const policyList = assignedPolicyList(Immutable.from(fullState));
    assert.deepEqual(policyList, expectedPolicyList, 'The returned value from the assignedPolicyList is as expected');
  });

  test('assignedPolicyList selector - source type assignment only', function(assert) {
    const expectedPolicyList = [
      {
        'id': 'placeholder',
        'name': 'Select a Policy',
        'policyType': 'edrPolicy'
      }
    ];
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'assignedPolicies': {
        'edrPolicy': {
          'referenceId': 'placeholder',
          'name': 'Select a Policy'
        }
      }
    };

    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload)
      .groupWizPolicyList(policyListPayload)
      .build();
    const policyList = assignedPolicyList(Immutable.from(fullState));
    assert.deepEqual(policyList, expectedPolicyList, 'The returned value from the assignedPolicyList is as expected');
  });

  test('isGroupCriteriaEmpty selector - no criteria', function(assert) {
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'groupCriteria': {},
      'assignedPolicies': {}
    };
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload)
      .groupWizPolicyList(policyListPayload)
      .build();
    assert.deepEqual(isGroupCriteriaEmpty(Immutable.from(fullState)), true, 'The returned value from the isGroupCriteriaEmpty is as expected');
  });

  test('isGroupCriteriaEmpty selector - with criteria', function(assert) {
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'groupCriteria': {
        'conjunction': 'AND',
        'criteria': [
          ['osType', 'IN', []]
        ]
      },
      'assignedPolicies': {}
    };
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload)
      .groupWizPolicyList(policyListPayload)
      .build();
    assert.deepEqual(isGroupCriteriaEmpty(Immutable.from(fullState)), false, 'The returned value from the isGroupCriteriaEmpty is as expected');
  });

  test('groupCriteriaValidator selector - no criteria', function(assert) {
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'groupCriteria': {},
      'assignedPolicies': {}
    };

    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload)
      .groupWizPolicyList(policyListPayload)
      .build();
    assert.deepEqual(groupCriteriaValidator(Immutable.from(fullState)), { isError: true }, 'The returned value from the groupCriteriaValidator is as expected');

    const isDefineGroupStepValidExpected = false;
    const isDefineGroupStepValidSelected = isDefineGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isDefineGroupStepValidSelected, isDefineGroupStepValidExpected, 'The returned value from the isDefineGroupStepValid selector is as expected');
  });

  test('groupCriteriaValidator selector - invalid criteria', function(assert) {
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'groupCriteria': {
        'conjunction': 'AND',
        'criteria': [
          ['osType', 'IN', []]
        ]
      },
      'assignedPolicies': {}
    };

    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload)
      .groupWizPolicyList(policyListPayload)
      .build();
    assert.deepEqual(groupCriteriaValidator(Immutable.from(fullState)), { isError: true }, 'The returned value from the groupCriteriaValidator is as expected');

    const isDefineGroupStepValidExpected = false;
    const isDefineGroupStepValidSelected = isDefineGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isDefineGroupStepValidSelected, isDefineGroupStepValidExpected, 'The returned value from the isDefineGroupStepValid selector is as expected');
  });

  test('groupCriteriaValidator selector - valid criteria', function(assert) {
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'groupCriteria': {
        'conjunction': 'AND',
        'criteria': [
          ['osType', 'IN', ['Windows']],
          ['osType', 'IN', ['Linux']],
          ['osDescription', 'ENDS_WITH', ['hebjc']],
          ['ipv4', 'NOT_IN', ['125.1.1.227,125.1.1.78\n']],
          ['hostname', 'EQUAL', ['trbkx']],
          ['osDescription', 'CONTAINS', ['xltbk']],
          ['ipv4', 'NOT_BETWEEN', ['1.1.1.45', '1.1.2.193']]
        ]
      },
      'assignedPolicies': {}
    };

    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload)
      .groupWizPolicyList(policyListPayload)
      .build();
    assert.deepEqual(groupCriteriaValidator(Immutable.from(fullState)), { isError: false }, 'The returned value from the groupCriteriaValidator is as expected');

    const isDefineGroupStepValidExpected = true;
    const isDefineGroupStepValidSelected = isDefineGroupStepValid(Immutable.from(fullState));
    assert.deepEqual(isDefineGroupStepValidSelected, isDefineGroupStepValidExpected, 'The returned value from the isDefineGroupStepValid selector is as expected');
  });

  test('policyAssignmentValidator selector - no assignments', function(assert) {
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'assignedPolicies': {}
    };

    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload)
      .groupWizPolicyList(policyListPayload)
      .build();
    assert.deepEqual(policyAssignmentValidator(Immutable.from(fullState)), { isError: false }, 'The returned value from the policyAssignmentValidator is as expected');

    const isApplyPolicyStepValidExpected = true;
    const isApplyPolicyStepValidSelected = isApplyPolicyStepValid(Immutable.from(fullState));
    assert.deepEqual(isApplyPolicyStepValidSelected, isApplyPolicyStepValidExpected, 'The returned value from the isApplyPolicyStepValid selector is as expected');
  });

  test('policyAssignmentValidator selector - invalid assignments', function(assert) {
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'assignedPolicies': {
        'edrPolicy': {
          'referenceId': 'placeholder',
          'name': 'Select a Policy'
        }
      }
    };

    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload)
      .groupWizPolicyList(policyListPayload)
      .build();
    assert.deepEqual(policyAssignmentValidator(Immutable.from(fullState)), { isError: true }, 'The returned value from the policyAssignmentValidator is as expected');

    const isApplyPolicyStepValidExpected = false;
    const isApplyPolicyStepValidSelected = isApplyPolicyStepValid(Immutable.from(fullState));
    assert.deepEqual(isApplyPolicyStepValidSelected, isApplyPolicyStepValidExpected, 'The returned value from the isApplyPolicyStepValid selector is as expected');
  });

  test('policyAssignmentValidator selector - valid assignments', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupWizGroup(groupPayload1)
      .groupWizPolicyList(policyListPayload)
      .build();
    assert.deepEqual(policyAssignmentValidator(Immutable.from(fullState)), { isError: false }, 'The returned value from the policyAssignmentValidator is as expected');

    const isApplyPolicyStepValidExpected = true;
    const isApplyPolicyStepValidSelected = isApplyPolicyStepValid(Immutable.from(fullState));
    assert.deepEqual(isApplyPolicyStepValidSelected, isApplyPolicyStepValidExpected, 'The returned value from the isApplyPolicyStepValid selector is as expected');
  });

  test('isLoadingGroupRanking selector', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .groupRanking('wait')
      .build();
    const isLoadingGroupRankingExpected = true;
    const isLoadingGroupRankingSelected = isLoadingGroupRanking(Immutable.from(fullState));
    assert.deepEqual(isLoadingGroupRankingSelected, isLoadingGroupRankingExpected, 'isLoadingGroupRanking is true when groupRankingStatus is wait');
  });

  test('groupRankingQuery selector', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .selectedSourceType('foo')
      .groupRankingWithData([{ id: 'fooID', otherParam: 'fooOtherParam' }, { id: 'fooID2', otherParam: 'fooOtherParam2' }])
      .build();
    const groupRankingQueryExpected = { policyType: 'foo', groupIds: ['fooID', 'fooID2'] };
    const groupRankingQueryResult = groupRankingQuery(Immutable.from(fullState));
    assert.deepEqual(groupRankingQueryExpected, groupRankingQueryResult, 'groupRankingQuery is as expected');
  });

  test('groupRankingViewQuery selector', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .selectedSourceType('foo')
      .groupRankingWithData([{ id: 'fooID', otherParam: 'fooOtherParam' }, { id: 'fooID2', otherParam: 'fooOtherParam2', isChecked: true }])
      .build();
    const groupRankingViewQueryExpected = { policyType: 'foo', groupIds: ['fooID2'] };
    const groupRankingQueryViewResult = groupRankingViewQuery(Immutable.from(fullState));
    assert.deepEqual(groupRankingViewQueryExpected, groupRankingQueryViewResult, 'groupRankingViewQuery is as expected');
  });

  test('groupRankingSelectedIndex selector', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .selectGroupRanking('foo2')
      .groupRankingWithData([{ name: 'foo', otherParam: 'fooOtherParam' }, { name: 'foo2', otherParam: 'fooOtherParam2' }])
      .build();
    const resultIndex = groupRankingSelectedIndex(Immutable.from(fullState));
    assert.deepEqual(1, resultIndex, 'groupRankingSelectedIndex is as expected');
  });
  test('groupRankingSelectedIndex selector top index', function(assert) {
    const fullState = new ReduxDataHelper()
      .groupWiz()
      .selectGroupRanking('foo')
      .groupRankingWithData([{ name: 'foo', otherParam: 'fooOtherParam' }, { name: 'foo2', otherParam: 'fooOtherParam2' }])
      .build();
    const resultIndex = groupRankingSelectedIndex(Immutable.from(fullState));
    assert.deepEqual(0, resultIndex, 'groupRankingSelectedIndex is top as expected');
  });
});

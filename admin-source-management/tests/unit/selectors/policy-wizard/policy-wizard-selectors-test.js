import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { lookup } from 'ember-dependency-lookup';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import moment from 'moment';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import {
  policy,
  visited,
  sourceTypes,
  selectedSourceType,
  enabledAvailableSettings,
  sortedSelectedSettings,
  nameValidator,
  descriptionValidator,
  steps,
  isIdentifyPolicyStepValid,
  isDefinePolicyStepValid,
  isWizardValid,
  isPolicyLoading,
  hasPolicyChanged,
  isPolicySettingsEmpty
} from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';

module('Unit | Selectors | policy-wizard/policy-wizard-selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

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
    const features = lookup('service:features');

    // windowsLogPolicy enabled so all types should be returned
    features.setFeatureFlags({ 'rsa.usm.allowWindowsLogPolicyCreation': true });
    let type0Expected = 'edrPolicy';
    const type1Expected = 'windowsLogPolicy';
    let fullState = new ReduxDataHelper().policyWiz().build();
    let sourceTypesExpected = _.cloneDeep(fullState.usm.policyWizard.sourceTypes);
    let sourceTypesSelected = sourceTypes(Immutable.from(fullState));
    assert.deepEqual(sourceTypesSelected.length, 2, 'All sourceTypes returned as expected');
    assert.deepEqual(sourceTypesSelected, sourceTypesExpected, 'The returned value from the sourceTypes selector is as expected');
    assert.deepEqual(sourceTypesSelected[0].policyType, type0Expected, `sourceTypes[0].policyType is ${type0Expected}`);
    assert.deepEqual(sourceTypesSelected[1].policyType, type1Expected, `sourceTypes[1].policyType is ${type1Expected}`);

    // windowsLogPolicy disabled so it should not be returned
    features.setFeatureFlags({ 'rsa.usm.allowWindowsLogPolicyCreation': false });
    type0Expected = 'edrPolicy';
    fullState = new ReduxDataHelper().policyWiz().build();
    sourceTypesExpected = _.cloneDeep(fullState.usm.policyWizard.sourceTypes.filter((sourceType) => sourceType.policyType !== 'windowsLogPolicy'));
    sourceTypesSelected = sourceTypes(Immutable.from(fullState));
    assert.deepEqual(sourceTypesSelected.length, 1, 'windowsLogPolicy sourceType filtered so only one type returned as expected');
    assert.deepEqual(sourceTypesSelected, sourceTypesExpected, 'The returned value from the sourceTypes selector is as expected');
    assert.deepEqual(sourceTypesSelected[0].policyType, type0Expected, `sourceTypes[0].policyType is ${type0Expected}`);
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

  test('enabledAvailableSettings only renders settings with isEnabled set', function(assert) {
    assert.expect(1);
    const state = {
      usm: {
        policyWizard: {
          availableSettings: [
            { index: 0, id: 'scanType', isEnabled: true },
            { index: 1, id: 'scanStartDate', isEnabled: false }
          ]
        }
      }
    };
    const result = enabledAvailableSettings(state);
    assert.deepEqual(result.length, 1, 'availableSettingToRender should not render when isEnabled is false');
  });

  test('sortedSelectedSettings renders settings in the order of index', function(assert) {
    assert.expect(1);
    const state = {
      usm: {
        policyWizard: {
          selectedSettings: [
            { index: 3, id: 'scanType' },
            { index: 1, id: 'scanStartDate' },
            { index: 2, id: 'cpuFrequency' }
          ]
        }
      }
    };
    const result = sortedSelectedSettings(state);
    assert.deepEqual(result[0].index, 1, 'selectedSettingToRender correctly sorted settings based on the index');
  });

  test('nameValidator selector', function(assert) {
    // isBlank & not visited
    let nameExpected = '';
    let visitedExpected = [];
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizVisited(visitedExpected)
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
    visitedExpected = ['policy.name'];
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizVisited(visitedExpected)
      .build();
    nameValidatorExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.policyWizard.nameRequired'
    };
    nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (isBlank & visited) returned value from the nameValidator selector is as expected');

    // nameExists
    nameExpected = 'existingName';
    visitedExpected = ['policy.name'];
    const policyListPayload = [
      {
        id: 'policy_000',
        name: 'existingName',
        description: '',
        createdOn: 1523655354337,
        lastModifiedOn: 1523655354337,
        lastPublishedOn: 1523655354337,
        dirty: false
      }
    ];

    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizVisited(visitedExpected)
      .policyWizPolicyList(policyListPayload)
      .build();
    nameValidatorExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.policyWizard.nameExists'
    };
    nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (nameExists) returned value from the nameValidator selector is as expected');

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
      .policyWiz()
      .policyWizName(nameExpected)
      .build();
    nameValidatorExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.policyWizard.nameExceedsMaxLength'
    };
    nameValidatorSelected = nameValidator(Immutable.from(fullState));
    assert.deepEqual(nameValidatorSelected, nameValidatorExpected, 'The (nameExceedsMaxLength) returned value from the nameValidator selector is as expected');
  });

  test('descriptionValidator selector', function(assert) {
    // isBlank & not visited
    let descExpected = '';
    let visitedExpected = [];
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizDescription(descExpected)
      .policyWizVisited(visitedExpected)
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
    visitedExpected = ['policy.description'];
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizDescription(descExpected)
      .policyWizVisited(visitedExpected)
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
    visitedExpected = ['policy.description'];
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizDescription(descExpected)
      .policyWizVisited(visitedExpected)
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
      .policyWiz()
      .policyWizDescription(descExpected)
      .build();
    descriptionValidatorExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.policyWizard.descriptionExceedsMaxLength'
    };
    descValidatorSelected = descriptionValidator(Immutable.from(fullState));
    assert.deepEqual(descValidatorSelected, descriptionValidatorExpected, 'The (descriptionExceedsMaxLength & visited) returned value from the descriptionValidator selector is as expected');
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
    // invalid name
    let nameExpected = '';
    let visitedExpected = ['policy.name'];
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizVisited(visitedExpected)
      .build();
    let isIdentifyPolicyStepValidExpected = false;
    let isIdentifyPolicyStepValidSelected = isIdentifyPolicyStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyPolicyStepValidSelected, isIdentifyPolicyStepValidExpected, 'The returned value from the isIdentifyPolicyStepValid selector is as expected');

    // valid name and invalid desc
    nameExpected = 'test';
    visitedExpected = ['policy.name', 'policy.description'];
    let descExpected = '';
    for (let index = 0; index < 220; index++) {
      descExpected += 'the-description-is-greater-than-8000-';
    }
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizDescription(descExpected)
      .policyWizVisited(visitedExpected)
      .build();
    isIdentifyPolicyStepValidExpected = false;
    isIdentifyPolicyStepValidSelected = isIdentifyPolicyStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyPolicyStepValidSelected, isIdentifyPolicyStepValidExpected, 'The returned value from the isIdentifyPolicyStepValid selector is as expected');

    // invalid name and invalid desc
    nameExpected = '';
    for (let index = 0; index < 10; index++) {
      nameExpected += 'the-name-is-greater-than-256-';
    }
    visitedExpected = ['policy.name', 'policy.description'];
    descExpected = '';
    for (let index = 0; index < 220; index++) {
      descExpected += 'the-description-is-greater-than-8000-';
    }

    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizDescription(descExpected)
      .policyWizVisited(visitedExpected)
      .build();
    isIdentifyPolicyStepValidExpected = false;
    isIdentifyPolicyStepValidSelected = isIdentifyPolicyStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyPolicyStepValidSelected, isIdentifyPolicyStepValidExpected, 'The returned value from the isIdentifyPolicyStepValid selector is as expected');

    // valid
    nameExpected = 'test name';
    visitedExpected = ['policy.name'];
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizName(nameExpected)
      .policyWizVisited(visitedExpected)
      .build();
    isIdentifyPolicyStepValidExpected = true;
    isIdentifyPolicyStepValidSelected = isIdentifyPolicyStepValid(Immutable.from(fullState));
    assert.deepEqual(isIdentifyPolicyStepValidSelected, isIdentifyPolicyStepValidExpected, 'The returned value from the isIdentifyPolicyStepValid selector is as expected');
  });

  test('isDefinePolicyStepValid selector', function(assert) {
    const nameExpected = 'test';
    const visitedExpected = ['policy.name'];
    // at least one setting required
    let selectedSettingsExpected = [];
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizSelectedSettings(selectedSettingsExpected)
      .policyWizName(nameExpected)
      .policyWizVisited(visitedExpected)
      .build();

    let isDefinePolicyStepValidExpected = false;
    let isDefinePolicyStepValidSelected = isDefinePolicyStepValid(fullState);
    assert.deepEqual(isDefinePolicyStepValidSelected, isDefinePolicyStepValidExpected, 'at least one setting should be selected');

    // start date selected but invalid
    selectedSettingsExpected = [
      { index: 1, id: 'scanType', label: 'adminUsm.policyWizard.edrPolicy.schedOrManScan', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'scanType', value: 'DISABLED' }] },
      { index: 2, id: 'scanStartDate', label: 'adminUsm.policyWizard.edrPolicy.effectiveDate', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/effective-date', defaults: [{ field: 'scanStartDate', value: moment().format('YYYY-MM-DD') }] }
    ];
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizSelectedSettings(selectedSettingsExpected)
      .policyWizName(nameExpected)
      .policyWizScanStartDate('')
      .policyWizVisited(visitedExpected)
      .build();

    isDefinePolicyStepValidExpected = false;
    isDefinePolicyStepValidSelected = isDefinePolicyStepValid(fullState);
    assert.deepEqual(isDefinePolicyStepValidSelected, isDefinePolicyStepValidExpected, 'start date is invalid');

    // endpoint server selected but invalid
    selectedSettingsExpected = [
      { index: 14, id: 'endpointServerHeader', label: 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', isHeader: true, isEnabled: true },
      { index: 15, id: 'primaryAddress', label: 'adminUsm.policyWizard.edrPolicy.primaryAddress', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/primary-address', defaults: [{ field: 'primaryAddress', value: '' }, { field: 'primaryNwServiceId', value: '' }] }
    ];
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizSelectedSettings(selectedSettingsExpected)
      .policyWizName(nameExpected)
      .policyWizPrimaryAddress('')
      .policyWizVisited(visitedExpected)
      .build();

    isDefinePolicyStepValidExpected = false;
    isDefinePolicyStepValidSelected = isDefinePolicyStepValid(fullState);
    assert.deepEqual(isDefinePolicyStepValidSelected, isDefinePolicyStepValidExpected, 'endpoint server is invalid');

    // endpoint server alias selected but invalid
    selectedSettingsExpected = [
      { index: 14, id: 'endpointServerHeader', label: 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', isHeader: true, isEnabled: true },
      { index: 15, id: 'primaryAddress', label: 'adminUsm.policyWizard.edrPolicy.primaryAddress', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/primary-address', defaults: [{ field: 'primaryAddress', value: '' }, { field: 'primaryNwServiceId', value: '' }, { field: 'primaryAlias', value: '' }] }
    ];
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizSelectedSettings(selectedSettingsExpected)
      .policyWizName(nameExpected)
      .policyWizPrimaryAddress('10.10.10.10')
      .policyWizPrimaryAlias('@foo')
      .policyWizVisited(visitedExpected)
      .build();

    isDefinePolicyStepValidExpected = false;
    isDefinePolicyStepValidSelected = isDefinePolicyStepValid(fullState);
    assert.deepEqual(isDefinePolicyStepValidSelected, isDefinePolicyStepValidExpected, 'Endpoint server alias is invalid');

    // ports selected but invalid
    selectedSettingsExpected = [
      { index: 14, id: 'endpointServerHeader', label: 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', isHeader: true, isEnabled: true },
      { index: 16, id: 'primaryHttpsPort', label: 'adminUsm.policyWizard.edrPolicy.primaryHttpsPort', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-ports', defaults: [{ field: 'primaryHttpsPort', value: 443 }] },
      { index: 18, id: 'primaryUdpPort', label: 'adminUsm.policyWizard.edrPolicy.primaryUdpPort', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-ports', defaults: [{ field: 'primaryUdpPort', value: 444 }] }
    ];
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizSelectedSettings(selectedSettingsExpected)
      .policyWizName(nameExpected)
      .policyWizPrimaryHttpsPort(0)
      .policyWizPrimaryUdpPort(444)
      .policyWizVisited(visitedExpected)
      .build();

    isDefinePolicyStepValidExpected = false;
    isDefinePolicyStepValidSelected = isDefinePolicyStepValid(fullState);
    assert.deepEqual(isDefinePolicyStepValidSelected, isDefinePolicyStepValidExpected, 'https port is invalid');

    // beacon intervals selected but invalid
    selectedSettingsExpected = [
      { index: 14, id: 'endpointServerHeader', label: 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', isHeader: true, isEnabled: true },
      { index: 17, id: 'primaryHttpsBeaconInterval', label: 'adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-beacons', defaults: [{ field: 'primaryHttpsBeaconInterval', value: 15 }, { field: 'primaryHttpsBeaconIntervalUnit', value: 'MINUTES' }] },
      { index: 19, id: 'primaryUdpBeaconInterval', label: 'adminUsm.policyWizard.edrPolicy.primaryUdpBeaconInterval', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-beacons', defaults: [{ field: 'primaryUdpBeaconInterval', value: 30 }, { field: 'primaryUdpBeaconIntervalUnit', value: 'SECONDS' }] }
    ];
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizSelectedSettings(selectedSettingsExpected)
      .policyWizName(nameExpected)
      .policyWizPrimaryHttpsBeaconInterval(0)
      .policyWizPrimaryHttpsBeaconIntervalUnit('HOURS')
      .policyWizPrimaryUdpBeaconInterval(15)
      .policyWizPrimaryUdpBeaconIntervalUnit('HOURS')
      .policyWizVisited(visitedExpected)
      .build();

    isDefinePolicyStepValidExpected = false;
    isDefinePolicyStepValidSelected = isDefinePolicyStepValid(fullState);
    assert.deepEqual(isDefinePolicyStepValidSelected, isDefinePolicyStepValidExpected, 'https beacon interval is invalid');
  });

  test('isWizardValid selector', function(assert) {
    const nameExpected = 'test';
    const visitedExpected = ['policy.name'];
    // at least one setting required
    const selectedSettingsExpected = [];
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizSelectedSettings(selectedSettingsExpected)
      .policyWizName(nameExpected)
      .policyWizVisited(visitedExpected)
      .build();

    const isWizardValidExpected = false;
    const isWizardValidSelected = isWizardValid(fullState);
    assert.deepEqual(isWizardValidSelected, isWizardValidExpected, 'at least one setting should be selected');
  });

  test('isPolicyLoading selector', function(assert) {
    let policyStatusExpected = 'wait';
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicyStatus(policyStatusExpected)
      .build();
    let isPolicyLoadingExpected = true;
    let isPolicyLoadingSelected = isPolicyLoading(Immutable.from(fullState));
    assert.deepEqual(isPolicyLoadingSelected, isPolicyLoadingExpected, 'isPolicyLoading is true when policyStatus is wait');

    policyStatusExpected = 'complete';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicyStatus(policyStatusExpected)
      .build();
    isPolicyLoadingExpected = false;
    isPolicyLoadingSelected = isPolicyLoading(Immutable.from(fullState));
    assert.deepEqual(isPolicyLoadingSelected, isPolicyLoadingExpected, 'isPolicyLoading is false when policyStatus is complete');
  });

  test('hasPolicyChanged selector', function(assert) {
    const policyPayload = {
      id: 'policy_014',
      policyType: 'edrPolicy',
      name: 'EMC Reston! 014',
      description: 'EMC Reston 014 of policy policy_014',
      blockingEnabled: false
    };
    const nochangeState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicy(policyPayload, true)
      .build();
    assert.equal(hasPolicyChanged(Immutable.from(nochangeState)), false, 'The returned value from the hasPolicyChanged selector is as false when no change has been made');

    const changeState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicy(policyPayload, false)
      .build();
    assert.equal(hasPolicyChanged(Immutable.from(changeState)), true, 'The returned value from the hasPolicyChanged selector is as true when change has been made');
  });

  test('isPolicySettingsEmpty selector - no settings', function(assert) {
    const selectedSettingsExpected = [];
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizSelectedSettings(selectedSettingsExpected)
      .build();
    assert.deepEqual(isPolicySettingsEmpty(Immutable.from(fullState)), true, 'The returned value from the isPolicySettingsEmpty is as expected');
  });

  test('isPolicySettingsEmpty selector - with settings', function(assert) {
    const selectedSettingsExpected = [
      { index: 14, id: 'endpointServerHeader', label: 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', isHeader: true, isEnabled: true },
      { index: 17, id: 'primaryHttpsBeaconInterval', label: 'adminUsm.policyWizard.edrPolicy.primaryHttpsBeaconInterval', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-beacons', defaults: [{ field: 'primaryHttpsBeaconInterval', value: 15 }, { field: 'primaryHttpsBeaconIntervalUnit', value: 'MINUTES' }] },
      { index: 19, id: 'primaryUdpBeaconInterval', label: 'adminUsm.policyWizard.edrPolicy.primaryUdpBeaconInterval', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-beacons', defaults: [{ field: 'primaryUdpBeaconInterval', value: 30 }, { field: 'primaryUdpBeaconIntervalUnit', value: 'SECONDS' }] }
    ];
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizSelectedSettings(selectedSettingsExpected)
      .build();
    assert.deepEqual(isPolicySettingsEmpty(Immutable.from(fullState)), false, 'The returned value from the isPolicySettingsEmpty is as expected');
  });

});

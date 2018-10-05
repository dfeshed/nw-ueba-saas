import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import moment from 'moment';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import {
  policy,
  visited,
  sourceTypes,
  selectedSourceType,
  enabledAvailableSettings,
  sortedSelectedSettings,
  scanType,
  startDate,
  startTime,
  interval,
  intervalType,
  isWeeklyInterval,
  runOnDaysOfWeek,
  weekOptions,
  cpuMax,
  cpuMaxVm,
  radioButtonValue,
  radioButtonOption,
  portValue,
  isPortValid,
  runIntervalConfig,
  nameValidator,
  descriptionValidator,
  steps,
  isIdentifyPolicyStepValid,
  endpointServersList,
  selectedEndpointSever,
  // TODO when implemented isDefinePolicyStepvalid,
  // TODO when implemented isApplyToGroupStepvalid,
  // TODO when implemented isReviewPolicyStepvalid,
  // TODO when implemented isWizardValid,
  isPolicyLoading
} from 'admin-source-management/reducers/usm/policy-wizard-selectors';
import {
  SCAN_SCHEDULE_CONFIG,
  CAPTURE_CODE_CONFIG,
  DOWNLOAD_MBR_CONFIG,
  FILTER_SIGNED_CONFIG,
  REQUEST_SCAN_CONFIG,
  BLOCKING_ENABLED_CONFIG,
  AGENT_MODE_CONFIG
} from 'admin-source-management/utils/settings';

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

  test('selectedEndpointSever selector', function(assert) {
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicy()
      .policyWizPrimaryAddress('10.10.10.10')
      .policyWizEndpointServers()
      .build();
    const endpointExpected = {
      'id': 'id1',
      'host': '10.10.10.10',
      'name': 'NWAPPLIANCE27455 - Endpoint Server'
    };
    const endpointSelected = selectedEndpointSever(Immutable.from(fullState));
    assert.deepEqual(endpointSelected, endpointExpected, 'The returned value from the selectedEndpointSever selector is as expected');

    // null selected endpoint
    const fullStateNullAddress = new ReduxDataHelper()
      .policyWiz()
      .policyWizPolicy()
      .policyWizPrimaryAddress(null)
      .policyWizEndpointServers()
      .build();
    const endpointNullExpected = null;
    const endpointNullSelected = selectedEndpointSever(Immutable.from(fullStateNullAddress));
    assert.deepEqual(endpointNullSelected, endpointNullExpected, 'The returned value from the selectedEndpointSever selector is null as expected');
  });

  test('endpointServersList selector', function(assert) {
    const hostExpected = '10.10.10.10';
    const idExpected = 'id1';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServers()
      .build();
    const endpointsSelected = endpointServersList(Immutable.from(fullState));
    assert.equal(endpointsSelected.length, 2, 'number of endpoints is as expected');
    assert.deepEqual(endpointsSelected[0].host, hostExpected, `endpointsSelected[0].host is ${hostExpected}`);
    assert.deepEqual(endpointsSelected[0].id, idExpected, `endpointsSelected[0].id is ${idExpected}`);
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

  test('scanType', function(assert) {
    const expectedScanType = 'MANUAL';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizScanType(expectedScanType)
      .build();
    const resultScanType = scanType(fullState);
    assert.deepEqual(resultScanType, expectedScanType, `should return scanType of ${expectedScanType}`);
  });

  test('captureFloatingCode', function(assert) {
    const expectedState = false;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizCaptureFloatingCode(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'captureFloatingCode');
    assert.deepEqual(result, expectedState, `should return captureFloatingCode of ${expectedState}`);
  });

  test('downloadMbr', function(assert) {
    const expectedState = true;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizDownloadMbr(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'downloadMbr');
    assert.deepEqual(result, expectedState, `should return downloadMbr of ${expectedState}`);
  });

  test('filterSignedHooks', function(assert) {
    const expectedState = true;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizFilterSignedHooks(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'filterSignedHooks');
    assert.deepEqual(result, expectedState, `should return filterSignedHooks of ${expectedState}`);
  });

  test('requestScanOnRegistration', function(assert) {
    const expectedState = true;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizRequestScanOnRegistration(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'requestScanOnRegistration');
    assert.deepEqual(result, expectedState, `should return requestScanOnRegistration of ${expectedState}`);
  });

  test('blockingEnabled', function(assert) {
    const expectedState = true;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizBlockingEnabled(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'blockingEnabled');
    assert.deepEqual(result, expectedState, `should return blockingEnabled of ${expectedState}`);
  });

  test('agentMode', function(assert) {
    const expectedState = false;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizAgentMode(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'agentMode');
    assert.deepEqual(result, expectedState, `should return agentMode of ${expectedState}`);
  });

  test('primaryHttpPort', function(assert) {
    const expectedValue = 555;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizHttpPort(expectedValue)
      .build();
    const result = portValue(fullState, 'primaryHttpPort');
    assert.deepEqual(result, expectedValue, `should return httpPort of ${expectedValue}`);
  });

  test('primaryUdpPort', function(assert) {
    const expectedValue = 666;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizUdpPort(expectedValue)
      .build();
    const result = portValue(fullState, 'primaryUdpPort');
    assert.deepEqual(result, expectedValue, `should return udpPort of ${expectedValue}`);
  });

  test('isPortValid returns correcly based on the validity of the port value', function(assert) {
    let portValue = 0;
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizUdpPort(portValue)
      .build();
    let result = isPortValid(fullState, 'primaryUdpPort');
    assert.deepEqual(result, false, `isPortValid returns ${result} when port value is ${portValue}`);

    portValue = -1;
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizUdpPort(portValue)
      .build();
    result = isPortValid(fullState, 'primaryUdpPort');
    assert.deepEqual(result, false, `isPortValid returns ${result} when port value is ${portValue}`);

    portValue = -77777;
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizUdpPort(portValue)
      .build();
    result = isPortValid(fullState, 'primaryUdpPort');
    assert.deepEqual(result, false, `isPortValid returns ${result} when port value is ${portValue}`);

    portValue = 77777;
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizUdpPort(portValue)
      .build();
    result = isPortValid(fullState, 'primaryUdpPort');
    assert.deepEqual(result, false, `isPortValid returns ${result} when port value is ${portValue}`);

    portValue = '';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizUdpPort(portValue)
      .build();
    result = isPortValid(fullState, 'primaryUdpPort');
    assert.deepEqual(result, false, `isPortValid returns ${result} when port value is empty string`);

    portValue = 443;
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizUdpPort(portValue)
      .build();
    result = isPortValid(fullState, 'primaryUdpPort');
    assert.deepEqual(result, true, `isPortValid returns ${result} when port value is ${portValue}`);
  });

  test('radioButtonOption returns the right radio button options based on the id', function(assert) {
    const result1 = radioButtonOption('scanType');
    assert.deepEqual(result1, SCAN_SCHEDULE_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for scanType id');
    const result2 = radioButtonOption('captureFloatingCode');
    assert.deepEqual(result2, CAPTURE_CODE_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for captureFloatingCode id');
    const result3 = radioButtonOption('downloadMbr');
    assert.deepEqual(result3, DOWNLOAD_MBR_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for downloadMbr id');
    const result4 = radioButtonOption('filterSignedHooks');
    assert.deepEqual(result4, FILTER_SIGNED_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for filterSignedHooks id');
    const result5 = radioButtonOption('requestScanOnRegistration');
    assert.deepEqual(result5, REQUEST_SCAN_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for requestScanOnRegistration id');
    const result6 = radioButtonOption('blockingEnabled');
    assert.deepEqual(result6, BLOCKING_ENABLED_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for blockingEnabled id');
    const result7 = radioButtonOption('agentMode');
    assert.deepEqual(result7, AGENT_MODE_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for agentMode id');
  });

  test('startDate', function(assert) {
    const startDateString = '2018-01-10';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizScanStartDate(startDateString)
      .build();
    const expectedISO = moment(startDateString, 'YYYY-MM-DD').toISOString();
    const resultISO = startDate(fullState);
    assert.deepEqual(resultISO, expectedISO, `should return scanStartDate as an ISO 8601 Date String of ${expectedISO}`);
  });

  test('startTime', function(assert) {
    const expectedStartTime = '10:49';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizScanStartTime(expectedStartTime)
      .build();
    const resultStartTime = startTime(fullState);
    assert.deepEqual(resultStartTime, expectedStartTime, `should return scanStartTime of ${expectedStartTime}`);
  });

  test('interval', function(assert) {
    const expectedInterval = 1;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizRecurrenceInterval(expectedInterval)
      .build();
    const resultInterval = interval(fullState);
    assert.deepEqual(resultInterval, expectedInterval, `should return recurrenceInterval of ${expectedInterval}`);
  });

  test('intervalType', function(assert) {
    const expectedIntervalType = 'DAYS';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizRecurrenceUnit(expectedIntervalType)
      .build();
    const resultIntervalType = intervalType(fullState);
    assert.deepEqual(resultIntervalType, expectedIntervalType, `should return recurrenceUnit of ${expectedIntervalType}`);
  });

  test('isWeeklyInterval', function(assert) {
    assert.expect(2);
    let expectedIntervalType = 'DAYS';
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizRecurrenceUnit(expectedIntervalType)
      .build();
    let expectedBoolean = false;
    let resultBoolean = isWeeklyInterval(fullState);
    assert.deepEqual(resultBoolean, expectedBoolean, `isWeeklyInterval(${expectedIntervalType}) should return ${expectedBoolean}`);

    expectedIntervalType = 'WEEKS';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizRecurrenceUnit(expectedIntervalType)
      .build();
    expectedBoolean = true;
    resultBoolean = isWeeklyInterval(fullState);
    assert.deepEqual(resultBoolean, expectedBoolean, `isWeeklyInterval(${expectedIntervalType}) should return ${expectedBoolean}`);
  });

  test('runOnDaysOfWeek', function(assert) {
    const expectedRunOnDaysOfWeek = ['SUNDAY'];
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizRunOnDaysOfWeek(expectedRunOnDaysOfWeek)
      .build();
    const resultRunOnDaysOfWeek = runOnDaysOfWeek(fullState);
    assert.deepEqual(resultRunOnDaysOfWeek, expectedRunOnDaysOfWeek, `should return runOnDaysOfWeek of ${expectedRunOnDaysOfWeek}`);
  });

  test('weekOptions', function(assert) {
    const expectedRecurrenceUnit = 'WEEKS';
    const expectedRunOnDaysOfWeek = ['SUNDAY'];
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizRecurrenceUnit(expectedRecurrenceUnit)
      .policyWizRunOnDaysOfWeek(expectedRunOnDaysOfWeek)
      .build();
    const expectedWeekOptions = {
      label: 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.week.SUNDAY',
      week: 'SUNDAY',
      isActive: true
    };
    const resultWeekOptions = weekOptions(fullState);
    assert.deepEqual(resultWeekOptions[0], expectedWeekOptions, 'should add label and isActive');
  });

  test('runIntervalConfig', function(assert) {
    const expectedRecurrenceUnit = 'WEEKS';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizRecurrenceUnit(expectedRecurrenceUnit)
      .build();
    const expectedConfig = {
      'options': [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24],
      'runLabel': 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.intervalText.WEEKS'
    };
    const resultConfig = runIntervalConfig(fullState);
    assert.deepEqual(resultConfig, expectedConfig, 'should return the processed run interval configuration');
  });

  test('cpuMax', function(assert) {
    const expectedCpuMax = 75;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizCpuMax(expectedCpuMax)
      .build();
    const resultCpuMax = cpuMax(fullState);
    assert.deepEqual(resultCpuMax, expectedCpuMax, `should return cpuMax of ${expectedCpuMax}`);
  });

  test('cpuMaxVm', function(assert) {
    const expectedCpuMaxVm = 85;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizCpuMaxVm(expectedCpuMaxVm)
      .build();
    const resultCpuMaxVm = cpuMaxVm(fullState);
    assert.deepEqual(resultCpuMaxVm, expectedCpuMaxVm, `should return cpuMaxVm of ${expectedCpuMaxVm}`);
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

});

import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { lookup } from 'ember-dependency-lookup';
import Immutable from 'seamless-immutable';
import moment from 'moment';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import {
  radioButtonValue,
  radioButtonOption
} from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';
import {
  scanType,
  startDate,
  startDateValidator,
  startTime,
  interval,
  intervalType,
  isWeeklyInterval,
  runOnDaysOfWeek,
  weekOptions,
  runIntervalConfig,
  cpuMax,
  cpuMaxVm,
  portValue,
  isPortValid,
  endpointServersList,
  selectedEndpointSever,
  primaryAddress,
  primaryAddressValidator,
  primaryAlias,
  isPrimaryAliasValid,
  beaconIntervalValue,
  beaconIntervalValueValidator,
  beaconIntervalUnits,
  selectedBeaconIntervalUnit,
  customConfig,
  customConfigValidator
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-selectors';
import {
  SCAN_SCHEDULE_CONFIG,
  // CAPTURE_CODE_CONFIG,
  SCAN_MBR_CONFIG,
  // FILTER_SIGNED_CONFIG,
  REQUEST_SCAN_CONFIG,
  BLOCKING_ENABLED_CONFIG,
  AGENT_MODE_CONFIG
} from 'admin-source-management/reducers/usm/policy-wizard/edrPolicy/edr-settings';

module('Unit | Selectors | policy-wizard/edrPolicy/edr-selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('edr policy steps', function(assert) {
    const expectedSteps = 2;
    const fullState = new ReduxDataHelper()
      .policyWiz('edrPolicy')
      .build();
    const { steps } = fullState.usm.policyWizard;
    assert.deepEqual(steps.length, expectedSteps, `edr policy should have ${expectedSteps} steps`);
  });

  test('scanType', function(assert) {
    const expectedScanType = 'DISABLED';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizScanType(expectedScanType)
      .build();
    const resultScanType = scanType(fullState);
    assert.deepEqual(resultScanType, expectedScanType, `should return scanType of ${expectedScanType}`);
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

  test('startDateValidator selector', function(assert) {
    const settingId = 'scanStartDate';
    const visited = [`policy.${settingId}`];
    let startDate = '';
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizScanStartDate(startDate)
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.policyWizard.edrPolicy.scanStartDateInvalidMsg'
    };
    let validActual = startDateValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for '${startDate}'`);

    // valid value
    startDate = '2018-01-10';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizScanStartDate(startDate)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, showError: false, errorMessage: '' };
    validActual = startDateValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${startDate}`);
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
      label: 'adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.week.SUNDAY',
      week: 'SUNDAY',
      isActive: true
    };
    const resultWeekOptions = weekOptions(fullState);
    assert.deepEqual(resultWeekOptions[0], expectedWeekOptions, 'should add label and isActive');
  });

  test('weekOptions no day mentioned', function(assert) {
    const expectedRecurrenceUnit = 'WEEKS';
    const expectedRunOnDaysOfWeek = null;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizRecurrenceUnit(expectedRecurrenceUnit)
      .policyWizRunOnDaysOfWeek(expectedRunOnDaysOfWeek)
      .build();
    const expectedWeekOptions = {
      label: 'adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.week.MONDAY',
      week: 'MONDAY',
      isActive: true
    };
    const resultWeekOptions = weekOptions(fullState);
    assert.deepEqual(resultWeekOptions[1], expectedWeekOptions, 'should add label and isActive');
  });

  test('runIntervalConfig', function(assert) {
    const expectedRecurrenceUnit = 'WEEKS';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizRecurrenceUnit(expectedRecurrenceUnit)
      .build();
    const expectedConfig = {
      'options': [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24],
      'runLabel': 'adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.intervalText.WEEKS'
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

  /* test('captureFloatingCode', function(assert) {
    const expectedState = false;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizCaptureFloatingCode(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'captureFloatingCode');
    assert.deepEqual(result, expectedState, `should return captureFloatingCode of ${expectedState}`);
  }); */

  test('scanMbr', function(assert) {
    const expectedState = true;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizScanMbr(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'scanMbr');
    assert.deepEqual(result, expectedState, `should return scanMbr of ${expectedState}`);
  });

  /* test('filterSignedHooks', function(assert) {
    const expectedState = true;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizFilterSignedHooks(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'filterSignedHooks');
    assert.deepEqual(result, expectedState, `should return filterSignedHooks of ${expectedState}`);
  }); */

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

  test('radioButtonOption returns the right radio button options based on the id', function(assert) {
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .build();
    const result1 = radioButtonOption(fullState, 'scanType');
    assert.deepEqual(result1, SCAN_SCHEDULE_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for scanType id');
    // const result2 = radioButtonOption(fullState, 'captureFloatingCode');
    // assert.deepEqual(result2, CAPTURE_CODE_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for captureFloatingCode id');
    const result3 = radioButtonOption(fullState, 'scanMbr');
    assert.deepEqual(result3, SCAN_MBR_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for scanMbr id');
    // const result4 = radioButtonOption(fullState, 'filterSignedHooks');
    // assert.deepEqual(result4, FILTER_SIGNED_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for filterSignedHooks id');
    const result5 = radioButtonOption(fullState, 'requestScanOnRegistration');
    assert.deepEqual(result5, REQUEST_SCAN_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for requestScanOnRegistration id');
    const result6 = radioButtonOption(fullState, 'blockingEnabled');
    assert.deepEqual(result6, BLOCKING_ENABLED_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for blockingEnabled id');
    const result7 = radioButtonOption(fullState, 'agentMode');
    assert.deepEqual(result7, AGENT_MODE_CONFIG, 'should return SCAN_SCHEDULE_CONFIG options for agentMode id');
  });

  test('primaryHttpsPort', function(assert) {
    const expectedValue = 555;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryHttpsPort(expectedValue)
      .build();
    const result = portValue(fullState, 'primaryHttpsPort');
    assert.deepEqual(result, expectedValue, `should return httpsPort of ${expectedValue}`);
  });

  test('primaryUdpPort', function(assert) {
    const expectedValue = 666;
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpPort(expectedValue)
      .build();
    const result = portValue(fullState, 'primaryUdpPort');
    assert.deepEqual(result, expectedValue, `should return udpPort of ${expectedValue}`);
  });

  test('isPortValid returns correcly based on the validity of the port value', function(assert) {
    let portValue = 0;
    const expectedErrorMsg = 'adminUsm.policyWizard.edrPolicy.portInvalidMsg';
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpPort(portValue)
      .build();
    let result = isPortValid(fullState, 'primaryUdpPort');
    assert.equal(result.isError, true, `isPortValid returns error when port value is ${portValue}`);
    assert.equal(result.errorMessage, expectedErrorMsg, 'isPortValid returns expected error message');

    portValue = -1;
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpPort(portValue)
      .build();
    result = isPortValid(fullState, 'primaryUdpPort');
    assert.equal(result.isError, true, `isPortValid returns error when port value is ${portValue}`);
    assert.equal(result.errorMessage, expectedErrorMsg, 'isPortValid returns expected error message');

    portValue = -77777;
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpPort(portValue)
      .build();
    result = isPortValid(fullState, 'primaryUdpPort');
    assert.equal(result.isError, true, `isPortValid does not return error when port value is ${portValue}`);
    assert.equal(result.errorMessage, expectedErrorMsg, 'isPortValid returns expected error message');

    portValue = 77777;
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpPort(portValue)
      .build();
    result = isPortValid(fullState, 'primaryUdpPort');
    assert.equal(result.isError, true, `isPortValid does not return error when port value is ${portValue}`);
    assert.equal(result.errorMessage, expectedErrorMsg, 'isPortValid returns expected error message');

    portValue = '';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpPort(portValue)
      .build();
    result = isPortValid(fullState, 'primaryUdpPort');
    assert.equal(result.isError, true, `isPortValid returns error when port value is ${portValue}`);
    assert.equal(result.errorMessage, expectedErrorMsg, 'isPortValid returns expected error message');

    portValue = 443;
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpPort(portValue)
      .build();
    result = isPortValid(fullState, 'primaryUdpPort');
    assert.equal(result.isError, false, `isPortValid does not return error when port value is ${portValue}`);
    assert.equal(result.errorMessage, '', 'isPortValid returns expected error message');
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

  test('primaryAddress', function(assert) {
    const primaryAddressExpected = '10.10.10.10';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryAddress(primaryAddressExpected)
      .build();
    const resultPrimaryAddress = primaryAddress(fullState, 'primaryAddress');
    assert.deepEqual(resultPrimaryAddress, primaryAddressExpected, `should return primary address ${primaryAddressExpected}`);
  });

  test('primaryAlias', function(assert) {
    const primaryAliasExpected = 'foo';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryAlias(primaryAliasExpected)
      .build();
    const resultPrimaryAlias = primaryAlias(fullState, 'primaryAlias');
    assert.deepEqual(resultPrimaryAlias, primaryAliasExpected, `should return primary alias ${primaryAliasExpected}`);
  });

  test('isPrimaryAliasValid selector', function(assert) {
    // invalid value
    let aliasValue = '@';
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryAlias(aliasValue)
      .build();
    let validExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.policyWizard.edrPolicy.primaryAliasInvalid'
    };
    let validActual = isPrimaryAliasValid(fullState);
    assert.deepEqual(validActual, validExpected, `should return expected value for '${aliasValue}'`);

    // valid value
    // alias can be empty since it is optional
    aliasValue = '';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryAlias(aliasValue)
      .build();
    validExpected = { isError: false, showError: false, errorMessage: '' };
    validActual = isPrimaryAliasValid(fullState);
    assert.deepEqual(validActual, validExpected, `should return expected value for emty ${aliasValue}`);
  });

  test('primaryAddressValidator selector', function(assert) {
    const settingId = 'primaryAddress';
    const visited = [`policy.${settingId}`];
    let addressValue = '';
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryAddress(addressValue)
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.policyWizard.edrPolicy.primaryAddressInvalidMsg'
    };
    let validActual = primaryAddressValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for '${addressValue}'`);

    // valid value
    addressValue = '10.10.10.10';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryAddress(addressValue)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, showError: false, errorMessage: '' };
    validActual = primaryAddressValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${addressValue}`);
  });

  test('beaconIntervalValue selector', function(assert) {
    let settingId = 'primaryHttpsBeaconInterval';
    let intervalExpected = 15;
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(intervalExpected)
      .build();
    let intervalActual = beaconIntervalValue(fullState, settingId);
    assert.deepEqual(intervalActual, intervalExpected, `${settingId} is ${intervalExpected}`);

    settingId = 'primaryUdpBeaconInterval';
    intervalExpected = 30;
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpBeaconInterval(intervalExpected)
      .build();
    intervalActual = beaconIntervalValue(fullState, settingId);
    assert.deepEqual(intervalActual, intervalExpected, `${settingId} is ${intervalExpected}`);
  });

  test('beaconIntervalValueValidator selector', function(assert) {
    // === HTTPS ===
    let settingId = 'primaryHttpsBeaconInterval';
    let visited = [`policy.${settingId}`];
    let intervalValue = 15;
    let intervalUnit = 'MINUTES';
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(intervalValue)
      .policyWizPrimaryHttpsBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    let validExpected = { isError: false, showError: false, errorMessage: '' };
    let validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);

    intervalValue = 0; // valid range is 1 minute to 24 hours  (or 1440 minutes or 86400 seconds)
    intervalUnit = 'MINUTES';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(intervalValue)
      .policyWizPrimaryHttpsBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: true, showError: true, errorMessage: `adminUsm.policyWizard.edrPolicy.${settingId}InvalidMsg` };
    validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);

    intervalValue = 1441; // valid range is 1 minute to 24 hours  (or 1440 minutes or 86400 seconds)
    intervalUnit = 'MINUTES';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(intervalValue)
      .policyWizPrimaryHttpsBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: true, showError: true, errorMessage: `adminUsm.policyWizard.edrPolicy.${settingId}InvalidMsg` };
    validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);

    intervalValue = 15; // valid range is 1 minute to 24 hours  (or 1440 minutes or 86400 seconds)
    intervalUnit = 'HOURS';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(intervalValue)
      .policyWizPrimaryHttpsBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, showError: false, errorMessage: '' };
    validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);

    intervalValue = 0; // valid range is 1 minute to 24 hours  (or 1440 minutes or 86400 seconds)
    intervalUnit = 'HOURS';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(intervalValue)
      .policyWizPrimaryHttpsBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: true, showError: true, errorMessage: `adminUsm.policyWizard.edrPolicy.${settingId}InvalidMsg` };
    validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);

    intervalValue = 25; // valid range is 1 minute to 24 hours  (or 1440 minutes or 86400 seconds)
    intervalUnit = 'HOURS';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryHttpsBeaconInterval(intervalValue)
      .policyWizPrimaryHttpsBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: true, showError: true, errorMessage: `adminUsm.policyWizard.edrPolicy.${settingId}InvalidMsg` };
    validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);

    // === UDP ===
    settingId = 'primaryUdpBeaconInterval';
    visited = [`policy.${settingId}`];
    intervalValue = 30;
    intervalUnit = 'SECONDS';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpBeaconInterval(intervalValue)
      .policyWizPrimaryUdpBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, showError: false, errorMessage: '' };
    validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);

    intervalValue = 4; // valid range is 5 seconds to 10 minutes (600 seconds)
    intervalUnit = 'SECONDS';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpBeaconInterval(intervalValue)
      .policyWizPrimaryUdpBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: true, showError: true, errorMessage: `adminUsm.policyWizard.edrPolicy.${settingId}InvalidMsg` };
    validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);

    intervalValue = 601; // valid range is 5 seconds to 10 minutes (600 seconds)
    intervalUnit = 'SECONDS';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpBeaconInterval(intervalValue)
      .policyWizPrimaryUdpBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: true, showError: true, errorMessage: `adminUsm.policyWizard.edrPolicy.${settingId}InvalidMsg` };
    validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);

    intervalValue = 5; // valid range is 5 seconds to 10 minutes (600 seconds)
    intervalUnit = 'MINUTES';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpBeaconInterval(intervalValue)
      .policyWizPrimaryUdpBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, showError: false, errorMessage: '' };
    validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);

    intervalValue = 0; // valid range is 5 seconds to 10 minutes (600 seconds)
    intervalUnit = 'MINUTES';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpBeaconInterval(intervalValue)
      .policyWizPrimaryUdpBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: true, showError: true, errorMessage: `adminUsm.policyWizard.edrPolicy.${settingId}InvalidMsg` };
    validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);

    intervalValue = 11; // valid range is 5 seconds to 10 minutes (600 seconds)
    intervalUnit = 'MINUTES';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpBeaconInterval(intervalValue)
      .policyWizPrimaryUdpBeaconIntervalUnit(intervalUnit)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: true, showError: true, errorMessage: `adminUsm.policyWizard.edrPolicy.${settingId}InvalidMsg` };
    validActual = beaconIntervalValueValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${intervalValue} ${intervalUnit}`);
  });

  test('beaconIntervalUnits selector', function(assert) {
    const i18n = lookup('service:i18n');

    let settingId = 'primaryHttpsBeaconInterval';
    const httpsMinsText = i18n.t(`adminUsm.policyWizard.edrPolicy.${settingId}_MINUTES`);
    const httpsHourText = i18n.t(`adminUsm.policyWizard.edrPolicy.${settingId}_HOURS`);
    let unitsExpected = [{ unit: 'MINUTES', label: httpsMinsText }, { unit: 'HOURS', label: httpsHourText }];
    let unitsActual = beaconIntervalUnits(settingId);
    assert.deepEqual(unitsActual, unitsExpected, `${settingId} units options are as expected`);

    settingId = 'primaryUdpBeaconInterval';
    const udpMinsText = i18n.t(`adminUsm.policyWizard.edrPolicy.${settingId}_SECONDS`);
    const udpHourText = i18n.t(`adminUsm.policyWizard.edrPolicy.${settingId}_MINUTES`);
    unitsExpected = [{ unit: 'SECONDS', label: udpMinsText }, { unit: 'MINUTES', label: udpHourText }];
    unitsActual = beaconIntervalUnits(settingId);
    assert.deepEqual(unitsActual, unitsExpected, `${settingId} units options are as expected`);
  });

  test('selectedBeaconIntervalUnit selector', function(assert) {
    let settingId = 'primaryHttpsBeaconInterval';
    let unitExpected = 'HOURS';
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryHttpsBeaconIntervalUnit(unitExpected)
      .build();
    let unitActual = selectedBeaconIntervalUnit(fullState, settingId);
    assert.deepEqual(unitActual.unit, unitExpected, `${settingId}Unit is ${unitExpected}`);

    settingId = 'primaryUdpBeaconInterval';
    unitExpected = 'MINUTES';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizPrimaryUdpBeaconIntervalUnit(unitExpected)
      .build();
    unitActual = selectedBeaconIntervalUnit(fullState, settingId);
    assert.deepEqual(unitActual.unit, unitExpected, `${settingId}Unit is ${unitExpected}`);
  });

  test('customConfig', function(assert) {
    const customConfigExpected = '"trackingConfig": {"uniqueFilterSeconds": 28800,"beaconStdDev": 2.0}';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizCustomConfig(customConfigExpected)
      .build();
    const resultCustomConfig = customConfig(fullState, 'customConfig');
    assert.deepEqual(resultCustomConfig, customConfigExpected, `should return custom config ${customConfigExpected}`);
  });

  test('customConfigValidator selector', function(assert) {
    const settingId = 'customConfig';
    const visited = [`policy.${settingId}`];
    // blank value not allowed
    let customSettingValue = ' ';
    let fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizCustomConfig(customSettingValue)
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.policyWizard.edrPolicy.customConfigInvalidMsg'
    };
    let validActual = customConfigValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for '${customSettingValue}'`);

    // value greater than 4k
    let testSetting = '';
    for (let index = 0; index < 110; index++) {
      testSetting += 'the-description-is-greater-than-4000-';
    }
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizCustomConfig(testSetting)
      .policyWizVisited(visited)
      .build();
    validActual = customConfigValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${testSetting}`);

    // valid value
    customSettingValue = 'foobar';
    fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizCustomConfig(customSettingValue)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, showError: false, errorMessage: '' };
    validActual = customConfigValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${customSettingValue}`);
  });

});
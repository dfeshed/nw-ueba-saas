import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import {
  radioButtonValue,
  radioButtonOption,
  logServersList,
  selectedLogServer,
  primaryDestination,
  primaryDestinationValidator
} from 'admin-source-management/reducers/usm/policy-wizard/windowsLogPolicy/windowsLog-selectors';
import {
  ENABLED_CONFIG,
  SEND_TEST_LOG_CONFIG
} from 'admin-source-management/reducers/usm/policy-wizard/windowsLogPolicy/windowsLog-settings';

module('Unit | Selectors | policy-wizard/windowsLogPolicy/windowsLog-selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('enabled', function(assert) {
    const expectedState = false;
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLog')
      .policyWizWinLogEnabled(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'enabled');
    assert.deepEqual(result, expectedState, `should return enabled ${expectedState}`);
  });

  test('sendTestLog', function(assert) {
    const expectedState = false;
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLog')
      .policyWizWinLogSendTestLog(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'sendTestLog');
    assert.deepEqual(result, expectedState, `should return sendTestLog ${expectedState}`);
  });

  test('radioButtonOption returns the right radio button options based on the id', function(assert) {
    const result1 = radioButtonOption('enabled');
    assert.deepEqual(result1, ENABLED_CONFIG, 'should return ENABLED_CONFIG options for enabled id');
    const result2 = radioButtonOption('sendTestLog');
    assert.deepEqual(result2, SEND_TEST_LOG_CONFIG, 'should return SEND_TEST_LOG_CONFIG options for sendTestLog id');
  });

  test('logServersList selector', function(assert) {
    const hostExpected = '10.10.10.10';
    const idExpected = 'id1';
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogLogServers()
      .build();
    const logServersSelected = logServersList(Immutable.from(fullState));
    assert.equal(logServersSelected.length, 2, 'number of log servers is as expected');
    assert.deepEqual(logServersSelected[0].host, hostExpected, `logServersSelected[0].host is ${hostExpected}`);
    assert.deepEqual(logServersSelected[0].id, idExpected, `logServersSelected[0].id is ${idExpected}`);
  });

  test('selectedLogServer selector', function(assert) {
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizPolicy()
      .policyWizWinLogPrimaryDestination('10.10.10.10')
      .policyWizWinLogLogServers()
      .build();
    const logServerExpected = {
      'id': 'id1',
      'host': '10.10.10.10',
      'name': 'NWAPPLIANCE55555 - Log Server'
    };
    const logServerSelected = selectedLogServer(Immutable.from(fullState));
    assert.deepEqual(logServerSelected, logServerExpected, 'The returned value from the selectedLogServer selector is as expected');

    // null selected endpoint
    const fullStateNullAddress = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizPolicy()
      .policyWizWinLogPrimaryDestination(null)
      .policyWizWinLogLogServers()
      .build();
    const logServerNullExpected = null;
    const logServerNullSelected = selectedLogServer(Immutable.from(fullStateNullAddress));
    assert.deepEqual(logServerNullSelected, logServerNullExpected, 'The returned value from the selectedLogServer selector is null as expected');
  });

  test('primaryDestination', function(assert) {
    const primaryDestinationExpected = '10.10.10.10';
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogPrimaryDestination(primaryDestinationExpected)
      .build();
    const resultPrimaryDestination = primaryDestination(fullState, 'primaryDestination');
    assert.deepEqual(resultPrimaryDestination, primaryDestinationExpected, `should return primary destination ${primaryDestinationExpected}`);
  });

  test('primaryDestinationValidator selector', function(assert) {
    const settingId = 'primaryDestination';
    const visited = [`policy.${settingId}`];
    let destinationValue = '';
    let fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogPrimaryDestination(destinationValue)
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.policyWizard.windowsLogPolicy.primaryDestinationInvalidMsg'
    };
    let validActual = primaryDestinationValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for '${destinationValue}'`);

    // valid value
    destinationValue = '10.10.10.10';
    fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogPrimaryDestination(destinationValue)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, showError: false, errorMessage: '' };
    validActual = primaryDestinationValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${destinationValue}`);
  });

});

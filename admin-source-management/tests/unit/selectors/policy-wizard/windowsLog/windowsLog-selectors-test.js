import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import {
  radioButtonValue,
  radioButtonOption,
  primaryLogServersList,
  selectedPrimaryLogServer,
  secondaryLogServersList,
  selectedSecondaryLogServer,
  windowsLogDestinationValidator,
  selectedProtocol,
  channels,
  channelFiltersValidator
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

  test('primaryLogServersList selector', function(assert) {
    const hostExpected = '10.10.10.10';
    const idExpected = 'id1';
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogLogServers()
      .build();
    const logServersSelected = primaryLogServersList(Immutable.from(fullState));
    assert.equal(logServersSelected.length, 2, 'number of primary log servers is as expected');
    assert.deepEqual(logServersSelected[0].host, hostExpected, `logServersSelected[0].host is ${hostExpected}`);
    assert.deepEqual(logServersSelected[0].id, idExpected, `logServersSelected[0].id is ${idExpected}`);
  });

  test('secondaryLogServersList selector', function(assert) {
    const hostExpected = '10.10.10.12';
    const idExpected = 'id2';
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogPrimaryDestination('10.10.10.10')
      .policyWizWinLogLogServers()
      .build();
    const logServersSelected = secondaryLogServersList(Immutable.from(fullState));
    assert.equal(logServersSelected.length, 1, 'number of secondary log servers is as expected');
    assert.deepEqual(logServersSelected[0].host, hostExpected, `logServersSelected[0].host is ${hostExpected}`);
    assert.deepEqual(logServersSelected[0].id, idExpected, `logServersSelected[0].id is ${idExpected}`);
  });

  test('selectedPrimaryLogServer selector', function(assert) {
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
    const logServerSelected = selectedPrimaryLogServer(Immutable.from(fullState));
    assert.deepEqual(logServerSelected, logServerExpected, 'The returned value from the selectedPrimaryLogServer selector is as expected');

    // null selected endpoint
    const fullStateNullAddress = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizPolicy()
      .policyWizWinLogPrimaryDestination(null)
      .policyWizWinLogLogServers()
      .build();
    const logServerNullExpected = null;
    const logServerNullSelected = selectedPrimaryLogServer(Immutable.from(fullStateNullAddress));
    assert.deepEqual(logServerNullSelected, logServerNullExpected, 'The returned value from the selectedPrimaryLogServer selector is null as expected');
  });

  test('selectedSecondaryLogServer selector', function(assert) {
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizPolicy()
      .policyWizWinLogPrimaryDestination('10.10.10.10')
      .policyWizWinLogSecondaryDestination('10.10.10.12')
      .policyWizWinLogLogServers()
      .build();
    const logServerExpected = {
      'id': 'id2',
      'host': '10.10.10.12',
      'name': 'NWAPPLIANCE66666- Log Server'
    };
    const logServerSelected = selectedSecondaryLogServer(Immutable.from(fullState));
    assert.deepEqual(logServerSelected, logServerExpected, 'The returned value from the selectedSecondaryLogServer selector is as expected');

    // null selected endpoint
    const fullStateNullAddress = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizPolicy()
      .policyWizWinLogSecondaryDestination(null)
      .policyWizWinLogLogServers()
      .build();
    const logServerNullExpected = null;
    const logServerNullSelected = selectedSecondaryLogServer(Immutable.from(fullStateNullAddress));
    assert.deepEqual(logServerNullSelected, logServerNullExpected, 'The returned value from the selectedPrimaryLogServer selector is null as expected');
  });

  test('windowsLogDestinationValidator selector with selectedId primaryDestination', function(assert) {
    const settingId = 'primaryDestination';
    const visited = [`policy.${settingId}`];
    let destinationValue = '';
    let fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogPrimaryDestination(destinationValue)
      .policyWizWinLogLogServers()
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.policyWizard.windowsLogPolicy.windowsLogDestinationInvalidMsg'
    };
    let validActual = windowsLogDestinationValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for '${destinationValue}'`);

    // valid value
    destinationValue = '10.10.10.10';
    fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogPrimaryDestination(destinationValue)
      .policyWizWinLogLogServers()
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, showError: false, errorMessage: '' };
    validActual = windowsLogDestinationValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${destinationValue}`);
  });

  test('windowsLogDestinationValidator selector with selectedId secondaryDestination', function(assert) {
    const settingId = 'secondaryDestination';
    const visited = [`policy.${settingId}`];
    let destinationValue = '';
    let fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogSecondaryDestination(destinationValue)
      .policyWizWinLogLogServers()
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      showError: true,
      errorMessage: 'adminUsm.policyWizard.windowsLogPolicy.windowsLogDestinationInvalidMsg'
    };
    let validActual = windowsLogDestinationValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for '${destinationValue}'`);

    // valid value
    destinationValue = '10.10.10.12';
    fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogSecondaryDestination(destinationValue)
      .policyWizWinLogLogServers()
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, showError: false, errorMessage: '' };
    validActual = windowsLogDestinationValidator(fullState, settingId);
    assert.deepEqual(validActual, validExpected, `${settingId} value validated as expected for ${destinationValue}`);
  });

  test('selectedProtocol', function(assert) {
    const expectedValue = 'TLS';
    const fullState = new ReduxDataHelper()
      .policyWiz()
      .policyWizWinLogProtocol(expectedValue)
      .build();
    const result = selectedProtocol(Immutable.from(fullState));
    assert.deepEqual(result, expectedValue, `should return protocol of ${expectedValue}`);
  });

  test('channels', function(assert) {
    const expectedValue = [ { channel: 'System', filterType: 'Include', eventId: 'ALL' } ];
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(expectedValue)
      .build();
    const result = channels(Immutable.from(fullState));
    assert.deepEqual(result, expectedValue, `should return channels with value ${expectedValue}`);
  });

  test('channelFiltersValidator selector with empty channel name', function(assert) {
    let newFilters = [ { channel: '', filterType: 'Include', eventId: 'ALL' }];
    const visited = ['policy.channelFilters'];
    let fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      invalidTableItem: '',
      errorMessage: 'adminUsm.policyWizard.windowsLogPolicy.invalidChannelFilter'
    };
    let validActual = channelFiltersValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newFilters} value validated as expected`);

    // valid value
    newFilters = [ { channel: 'System', filterType: 'Include', eventId: 'ALL' }];
    fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, invalidTableItem: '', errorMessage: '' };
    validActual = channelFiltersValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newFilters} value validated as expected`);
  });

  test('channelFiltersValidator selector with empty event id', function(assert) {
    let newFilters = [ { channel: 'System', filterType: 'Include', eventId: '' }];
    const visited = ['policy.channelFilters'];
    let fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      invalidTableItem: '',
      errorMessage: 'adminUsm.policyWizard.windowsLogPolicy.invalidChannelFilter'
    };
    let validActual = channelFiltersValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newFilters} value validated as expected`);

    // valid value
    newFilters = [ { channel: 'System', filterType: 'Include', eventId: '2' }];
    fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, invalidTableItem: '', errorMessage: '' };
    validActual = channelFiltersValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newFilters} value validated as expected`);
  });

  test('channelFiltersValidator selector with invalid event id', function(assert) {
    let newFilters = [ { channel: 'System', filterType: 'Include', eventId: 'foo$' }];
    const visited = ['policy.channelFilters'];
    let fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      invalidTableItem: 'foo$',
      errorMessage: 'adminUsm.policyWizard.windowsLogPolicy.invalidEventId'
    };
    let validActual = channelFiltersValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newFilters} value validated as expected`);

    // valid value
    newFilters = [ { channel: 'System', filterType: 'Include', eventId: '2' }];
    fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, invalidTableItem: '', errorMessage: '' };
    validActual = channelFiltersValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newFilters} value validated as expected`);
  });

  test('channelFiltersValidator selector with exclude filter and null event id', function(assert) {
    let newFilters = [ { channel: 'System', filterType: 'Exclude', eventId: '' }];
    const visited = ['policy.channelFilters'];
    let fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      invalidTableItem: '',
      errorMessage: 'adminUsm.policyWizard.windowsLogPolicy.invalidChannelFilter'
    };
    let validActual = channelFiltersValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newFilters} value validated as expected`);

    // valid value
    newFilters = [ { channel: 'System', filterType: 'Include', eventId: '2' }];
    fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, invalidTableItem: '', errorMessage: '' };
    validActual = channelFiltersValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newFilters} value validated as expected`);
  });

  test('channelFiltersValidator selector with multiple event ids', function(assert) {
    const newFilters = [ { channel: 'System', filterType: 'Exclude', eventId: '2,3,4' }];
    const visited = ['policy.channelFilters'];
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visited)
      .build();
    const validExpected = {
      isError: false,
      invalidTableItem: '',
      errorMessage: ''
    };
    const validActual = channelFiltersValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newFilters} value validated as expected`);
  });

  test('channelFiltersValidator selector with multiple event ids and a special character', function(assert) {
    const newFilters = [ { channel: 'System', filterType: 'Exclude', eventId: '2,3,4%' }];
    const visited = ['policy.channelFilters'];
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLogPolicy')
      .policyWizWinLogChannelFilters(newFilters)
      .policyWizVisited(visited)
      .build();
    const validExpected = {
      isError: true,
      invalidTableItem: '2,3,4%',
      errorMessage: 'adminUsm.policyWizard.windowsLogPolicy.invalidEventId'
    };
    const validActual = channelFiltersValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newFilters} value validated as expected`);
  });
});

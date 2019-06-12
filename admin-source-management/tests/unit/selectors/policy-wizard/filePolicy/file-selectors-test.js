import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import {
  radioButtonValue,
  radioButtonOption
} from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';
import {
  fileSources,
  fileSourcesList,
  selectedFileSource
} from 'admin-source-management/reducers/usm/policy-wizard/filePolicy/file-selectors';
import {
  ENABLED_CONFIG,
  SEND_TEST_LOG_CONFIG
} from 'admin-source-management/reducers/usm/policy-wizard/filePolicy/file-settings';

module('Unit | Selectors | policy-wizard/filePolicy/file-selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('file policy steps', function(assert) {
    const expectedSteps = 3;
    const fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .build();
    const { steps } = fullState.usm.policyWizard;
    assert.deepEqual(steps.length, expectedSteps, `file policy should have ${expectedSteps} steps`);
  });

  test('enabled', function(assert) {
    const expectedState = false;
    const fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileEnabled(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'enabled');
    assert.deepEqual(result, expectedState, `should return enabled ${expectedState}`);
  });

  test('sendTestLog', function(assert) {
    const expectedState = false;
    const fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSendTestLog(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'sendTestLog');
    assert.deepEqual(result, expectedState, `should return sendTestLog ${expectedState}`);
  });

  test('radioButtonOption returns the right radio button options based on the id', function(assert) {
    const fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .build();
    const result1 = radioButtonOption(fullState, 'enabled');
    assert.deepEqual(result1, ENABLED_CONFIG, 'should return ENABLED_CONFIG options for enabled id');
    const result2 = radioButtonOption(fullState, 'sendTestLog');
    assert.deepEqual(result2, SEND_TEST_LOG_CONFIG, 'should return SEND_TEST_LOG_CONFIG options for sendTestLog id');
  });

  test('fileSources', function(assert) {
    const expectedValue = [ { fileType: 'apache', fileEncoding: 'UTF-8', enabled: true, startOfEvents: false, sourceName: 'apache-server-1', exclusionFilter: ['filter-1', 'filter-2'] } ];
    const fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSources(expectedValue)
      .build();
    const result = fileSources(Immutable.from(fullState));
    assert.deepEqual(result, expectedValue, `should return fileSources with value ${expectedValue}`);
  });

  test('fileSourcesList selector', function(assert) {
    const nameExpected = 'accurev';
    const prettyNameExpected = 'accurev';
    const fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .build();
    const fileSources = fileSourcesList(Immutable.from(fullState));
    assert.equal(fileSources.length, 3, 'number of file type sources is as expected');
    assert.deepEqual(fileSources[0].name, nameExpected, `fileSources[0].name is ${nameExpected}`);
    assert.deepEqual(fileSources[0].prettyName, prettyNameExpected, `fileSources[0].prettyName is ${prettyNameExpected}`);
  });

  test('selectedFileSource selector', function(assert) {
    const fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .build();
    const fileTypeExpected = {
      'name': 'apache',
      'prettyName': 'apache'
    };
    const fileTypeSelected = selectedFileSource(Immutable.from(fullState));
    assert.deepEqual(fileTypeSelected, fileTypeExpected, 'The returned value from the selectedFileSource selector is as expected');
  });
});

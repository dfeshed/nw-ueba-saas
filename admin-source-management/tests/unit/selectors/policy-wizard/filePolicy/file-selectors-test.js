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
  selectedFileSource,
  selectedFileSourceDefaults,
  sources,
  sourceNameValidator
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
    const expectedValue = [ { fileType: 'apache', fileTypePrettyName: 'Apache Web Server', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: 'apache-server-1', exclusionFilters: ['filter-1', 'filter-2'] } ];
    const fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSources(expectedValue)
      .policyWizFileSourceTypes()
      .build();
    const result = fileSources(Immutable.from(fullState));
    assert.deepEqual(result, expectedValue, `should return fileSources with value ${expectedValue}`);
  });

  test('fileSourcesList selector', function(assert) {
    const nameExpected = 'accurev';
    const prettyNameExpected = 'AccuRev';
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
      'prettyName': 'Apache Web Server',
      'paths': [
        '/c/Program Files/Apache Group/Apache[2-9]/*.log',
        '/c/Program Files/Apache Group/Apache[2-9]/logs/*.log'
      ]
    };
    const fileTypeSelected = selectedFileSource(Immutable.from(fullState));
    assert.deepEqual(fileTypeSelected, fileTypeExpected, 'The returned value from the selectedFileSource selector is as expected');
  });

  test('selectedFileSourceDefaults selector', function(assert) {
    const fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSourceTypes()
      .build();
    const defaultsExpected = {
      'enabled': true,
      'exclusionFilters': [],
      'fileEncoding': 'UTF-8 / ASCII',
      'startOfEvents': true,
      'sourceName': '',
      'fileType': 'apache',
      'paths': [
        '/c/Program Files/Apache Group/Apache[2-9]/*.log',
        '/c/Program Files/Apache Group/Apache[2-9]/logs/*.log'
      ]
    };
    const defaults = selectedFileSourceDefaults(Immutable.from(fullState));
    assert.deepEqual(defaults, defaultsExpected, 'The returned defaults value from the selectedFileSourceDefaults selector is as expected');
  });

  test('sources', function(assert) {
    const expectedValue = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: 'apache-server-1', exclusionFilters: ['filter-1', 'filter-2'], paths: ['path1', 'path2'] } ];
    const fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSources(expectedValue)
      .build();
    const result = sources(Immutable.from(fullState));
    assert.deepEqual(result, expectedValue, `should return sources with value ${expectedValue}`);
  });

  test('sourceNameValidator selector with invalid source name', function(assert) {
    let newSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: 'foo$', exclusionFilters: ['filter-1', 'filter-2'], paths: ['path1', 'path2'] } ];
    // let newFilters = [ { channel: 'System', filterType: 'INCLUDE', eventId: 'foo$' }];
    const visited = ['policy.sources'];
    let fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSources(newSource)
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      invalidTableItem: 'foo$',
      errorMessage: 'adminUsm.policyWizard.filePolicy.invalidSourceName',
      dirPathEmptyMsg: '',
      dirPathLength: '',
      invalidPath: 'invalid'
    };
    let validActual = sourceNameValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newSource} value validated as expected`);

    // valid value
    newSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: 'apache-server-1', exclusionFilters: ['filter-1', 'filter-2'], paths: ['path1', 'path2'] } ];
    fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSources(newSource)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, invalidTableItem: 'invalid', errorMessage: '', dirPathEmptyMsg: '', dirPathLength: '', invalidPath: 'invalid' };
    validActual = sourceNameValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newSource} value validated as expected`);


    // invalid ipv4 address for source name
    newSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: '10:42.42.42', exclusionFilters: ['filter-1', 'filter-2'], paths: ['path1', 'path2'] } ];
    fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSources(newSource)
      .policyWizVisited(visited)
      .build();
    validExpected = {
      isError: true,
      invalidTableItem: '10:42.42.42',
      errorMessage: 'adminUsm.policyWizard.filePolicy.invalidSourceName',
      dirPathEmptyMsg: '',
      dirPathLength: '',
      invalidPath: 'invalid'
    };
    validActual = sourceNameValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newSource} value validated as expected`);

    // invalid ipv6 address for source name
    newSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: '1200::AB00:1234::2552:7777:1313', exclusionFilters: ['filter-1', 'filter-2'], paths: ['path1', 'path2'] } ];
    fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSources(newSource)
      .policyWizVisited(visited)
      .build();
    validExpected = {
      isError: true,
      invalidTableItem: '1200::AB00:1234::2552:7777:1313',
      errorMessage: 'adminUsm.policyWizard.filePolicy.invalidSourceName',
      dirPathEmptyMsg: '',
      dirPathLength: '',
      invalidPath: 'invalid'
    };
    validActual = sourceNameValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newSource} value validated as expected`);

    // valid ipv6 address
    newSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: '1200:0000:AB00:1234:0000:2552:7777:1313', exclusionFilters: ['filter-1', 'filter-2'], paths: ['path1', 'path2'] } ];
    fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSources(newSource)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, invalidTableItem: 'invalid', errorMessage: '', dirPathEmptyMsg: '', dirPathLength: '', invalidPath: 'invalid' };
    validActual = sourceNameValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newSource} value validated as expected`);
  });

  test('sourceNameValidator selector with invalid directory paths', function(assert) {
    // when path is empty
    let newSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: 'foo', exclusionFilters: ['filter-1', 'filter-2'], paths: [] } ];
    const visited = ['policy.sources'];
    let fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSources(newSource)
      .policyWizVisited(visited)
      .build();
    let validExpected = {
      isError: true,
      invalidTableItem: 'invalid',
      errorMessage: '',
      dirPathEmptyMsg: 'adminUsm.policyWizard.filePolicy.dirPathEmpty',
      dirPathLength: 0,
      invalidPath: 'invalid'
    };
    let validActual = sourceNameValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newSource} value validated as expected`);

    // invalid - when one of the directory paths is empty
    newSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: '10.42.42.42', exclusionFilters: ['filter-1', 'filter-2'], paths: ['path1', ''] } ];
    fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSources(newSource)
      .policyWizVisited(visited)
      .build();
    validExpected = {
      isError: true,
      invalidTableItem: 'invalid',
      dirPathLength: '',
      dirPathEmptyMsg: '',
      invalidPath: '',
      errorMessage: 'adminUsm.policyWizard.filePolicy.invalidDirPath'
    };
    validActual = sourceNameValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newSource} value validated as expected`);

    // valid value
    newSource = [ { fileType: 'apache', fileEncoding: 'UTF-8 / ASCII', enabled: true, startOfEvents: false, sourceName: 'apache-server-1', exclusionFilters: ['filter-1', 'filter-2'], paths: ['path1', 'path2'] } ];
    fullState = new ReduxDataHelper()
      .policyWiz('filePolicy')
      .policyWizFileSources(newSource)
      .policyWizVisited(visited)
      .build();
    validExpected = { isError: false, invalidTableItem: 'invalid', errorMessage: '', invalidPath: 'invalid', dirPathEmptyMsg: '', dirPathLength: '' };
    validActual = sourceNameValidator(fullState);
    assert.deepEqual(validActual, validExpected, `${newSource} value validated as expected`);
  });
});

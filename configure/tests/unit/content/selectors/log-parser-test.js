import { module, test } from 'qunit';
import {
  hasRuleFormats,
  hasSelectedParserRule,
  filterDeletedRule,
  isDeletingParserRule,
  isDeletingParserRuleError,
  isOotb,
  selectedParserRuleFormat,
  logParsers,
  deviceTypes,
  deviceClasses,
  availableDeviceTypes,
  isTransactionUnderway,
  selectedLogParser,
  selectedLogParserName
} from 'configure/reducers/content/log-parser-rules/selectors';

module('Unit | Selectors | log-parser-rules');

const logParserRules = {
  logParsers: [{ name: 'ciscopix' }],
  selectedLogParserIndex: 0,
  ruleFormats: [{ type: 'fOO', matches: 'result', name: 'abc' }, { type: 'fOO2', matches: 'result2', name: 'def' }],
  selectedParserRuleIndex: 0,
  deleteRuleStatus: 'error',
  selectedFormat: 'abc',
  deviceTypes: [{ name: 'ciscopix', desc: 'Cisco', category: 'Intrusion' }, {
    name: 'apache',
    desc: 'Apache Web Server',
    category: 'Server'
  }],
  deviceClasses: ['Intrusion', 'Wireless Access'],
  parserRules: [
    {
      name: 'foo',
      pattern: {
        format: 'Foo'
      },
      outOfBox: true
    },
    {
      name: 'foo2',
      pattern: {
        format: 'Foo2'
      },
      outOfBox: false
    }
  ],
  isTransactionUnderway: false
};

const state = (state = logParserRules) => ({
  configure: {
    content: {
      logParserRules: {
        ...logParserRules,
        ...state
      }
    }
  }
});

test('Basic selector expectations', function(assert) {

  assert.equal(logParsers(state()), logParserRules.logParsers, 'The logParsers selector returns the logParsers from state');
  assert.equal(deviceTypes(state()), logParserRules.deviceTypes, 'The deviceTypes selector returns the deviceTypes from state');
  assert.equal(deviceClasses(state()), logParserRules.deviceClasses, 'The deviceClasses selector returns the deviceClasses from state');
  assert.equal(isTransactionUnderway(state()), logParserRules.isTransactionUnderway, 'The isTransactionUnderway selector returns the isTransactionUnderway value from state');
  assert.deepEqual(selectedLogParser(state()), { name: 'ciscopix' }, 'The log parser with the selected index is returned');
  assert.equal(selectedLogParserName(state()), 'ciscopix', 'The name of the selected parser is returned');
  assert.equal(selectedLogParserName(state({ logParsers: [] })), '', 'An empty string is returned if the selected log parser cannot be found');
});

test('The availableDeviceTypes selector filters out any entries in logParsers with the same name property', function(assert) {
  assert.deepEqual(availableDeviceTypes(state()), [{ name: 'apache', desc: 'Apache Web Server', category: 'Server' }]);
});

test('Test Booleans hasRuleFormats', function(assert) {
  assert.equal(hasRuleFormats(state()), true, 'The formats are loaded and hasRuleFormats is true');
  assert.equal(hasRuleFormats(state({ ruleFormats: null })), false, 'The formats are not loaded and hasRuleFormats is false');
});

test('Test Booleans hasSelectedParserRule', function(assert) {
  assert.equal(hasSelectedParserRule(state()), true, 'A parser rule is selected and hasSelectedParserRule is true');
  assert.equal(hasSelectedParserRule(state({ selectedParserRuleIndex: -1 })), false, 'A parser rule is not selected and hasSelectedParserRule is false');
});

test('filterDeletedRule by selectedParserRuleIndex', function(assert) {
  const filteredRule = [
    {
      name: 'foo2',
      pattern: {
        format: 'Foo2'
      },
      outOfBox: false
    }
  ];
  assert.deepEqual(filterDeletedRule(state()), filteredRule, 'The rule with index === 0 was filtered');
});
test('isDeletingParserRule wait', function(assert) {
  assert.equal(isDeletingParserRule(state({ deleteRuleStatus: 'wait' })), true, 'waiting for delete confirmation');
});
test('isDeletingParserRuleError error', function(assert) {
  assert.equal(isDeletingParserRuleError(state()), true, 'error getting delete confirmation');
});
test('isDeletingParserRule wait', function(assert) {
  assert.equal(isDeletingParserRule(state()), false, 'waiting for delete no confirmation');
});
test('isDeletingParserRuleError error', function(assert) {
  assert.equal(isDeletingParserRuleError(state({ deleteRuleStatus: 'wait' })), false, 'no error getting delete confirmation');
});

test('isOotb', function(assert) {
  assert.equal(isOotb(state()), true, 'is ootb');
});

test('selectedParserRuleFormat with _selectedFormat', function(assert) {
  const selectedFormat = { type: 'fOO', matches: 'result', name: 'abc' };
  assert.deepEqual(selectedParserRuleFormat(state()), selectedFormat, 'OK');
});
test('selectedParserRuleFormat without _selectedFormat', function(assert) {
  const selectedFormat = { type: 'fOO', matches: 'result', name: 'abc' };
  assert.deepEqual(selectedParserRuleFormat(state({ selectedFormat: null })), selectedFormat, 'OK');
});

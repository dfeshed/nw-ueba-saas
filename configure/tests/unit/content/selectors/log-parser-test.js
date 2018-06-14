import { module, test } from 'qunit';
import {
  hasRuleFormats,
  hasSelectedParserRule,
  filterDeletedRule,
  isDeletingParserRule,
  isDeletingParserRuleError,
  isParserRuleOutOfBox,
  logParsers,
  deviceTypes,
  deviceClasses,
  availableDeviceTypes,
  isTransactionUnderway,
  selectedLogParser,
  selectedLogParserName,
  sampleLogs,
  isHighlighting,
  highlightedLogs,
  metas
} from 'configure/reducers/content/log-parser-rules/selectors';

module('Unit | Selectors | log-parser-rules');

const logParserRules = {
  logParsers: [{ name: 'ciscopix' }],
  selectedLogParserIndex: 0,
  ruleFormats: [{ type: 'fOO', matches: 'result', name: 'abc' }, { type: 'fOO2', matches: 'result2', name: 'def' }],
  selectedParserRuleIndex: 0,
  deleteRuleStatus: 'error',
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
  isTransactionUnderway: false,
  sampleLogs: 'Testing 123',
  metas: [{ metaName: 'device.ip' }]
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
  assert.equal(sampleLogs(state()), 'Testing 123', 'The baseline sample logs are returned');
  assert.equal(isHighlighting(state({ sampleLogsStatus: 'wait' })), true, 'isHighlighting is true when the sampleLogsStatus is wait');
  assert.equal(isHighlighting(state({ sampleLogsStatus: 'completed' })), false, 'isHighlighting is true when the sampleLogsStatus is not wait');
  assert.equal(isDeletingParserRule(state({ deleteRuleStatus: 'wait' })), true);
  assert.equal(isDeletingParserRule(state({ deleteRuleStatus: 'completed' })), false);
  assert.equal(isDeletingParserRuleError(state({ deleteRuleStatus: 'error' })), true);
  assert.equal(isDeletingParserRuleError(state({ deleteRuleStatus: 'completed' })), false);
  assert.equal(isParserRuleOutOfBox(state({ parserRules: [{ outOfBox: true }], selectedLogParserIndex: 0 })), true);
  assert.equal(isParserRuleOutOfBox(state({ parserRules: [{ outOfBox: false }], selectedLogParserIndex: 0 })), false);
  assert.equal(metas(state()), logParserRules.metas);
});

test('The availableDeviceTypes selector filters out any entries in logParsers with the same name property', function(assert) {
  assert.deepEqual(availableDeviceTypes(state()), [{ name: 'apache', desc: 'Apache Web Server', category: 'Server' }]);
});

test('Test Booleans hasRuleFormats', function(assert) {
  assert.equal(hasRuleFormats(state()), true, 'The formats are loaded and hasRuleFormats is true');
  assert.equal(hasRuleFormats(state({ ruleFormats: [] })), false, 'The formats are not loaded and hasRuleFormats is false');
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

test('the highlightedLogs selector properly reworks the class names in the highlighted text', function(assert) {
  const testState = {
    sampleLogs: 'This is an <span class=\'highlight_capture_EXAMPLE\'><span class=\'highlight_literal_EXAMPLE\'>example</span> of highlighting</span> for ' +
    'a test',
    selectedParserRuleIndex: 0,
    parserRules: [{ name: 'test' }, { name: 'EXAMPLE' }]
  };
  assert.equal(highlightedLogs(state(testState)), 'This is an <span class=\'highlight-capture EXAMPLE\'><span class=\'highlight-literal EXAMPLE\'>example</span> of highlighting</span> for a test',
    'The class names in the html of the logs get reworked to hypnenate and remove the parser rule name when that parser is not selected');
  assert.equal(highlightedLogs(state({ ...testState, selectedParserRuleIndex: 1 })), 'This is an <span class=\'highlight-capture is-selected\'>' +
    '<span class=\'highlight-literal is-selected\'>example</span> of highlighting</span> for a test',
    'The class names in the html of the logs get reworked add an is-selected class name if the selected parser rule name is found in the class name');
});

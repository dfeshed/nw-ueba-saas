import { module, test } from 'qunit';
import {
  hasRuleFormats,
  hasSelectedParserRule,
  filterDeletedRule,
  isDeletingParserRule,
  isDeletingParserRuleError,
  isOotb,
  selectedParserRuleFormat
} from 'configure/reducers/content/log-parser-rules/selectors';

module('Unit | Selectors | log-parser-rules');

const state = {
  configure: {
    content: {
      logParserRules: {
        ruleFormats: [{ type: 'fOO', matches: 'result', name: 'abc' }, { type: 'fOO2', matches: 'result2', name: 'def' }],
        selectedParserRuleIndex: 0,
        deleteRuleStatus: 'error',
        selectedFormat: 'abc',
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
        ]
      }
    }
  }
};

const stateNoSelectedFormat = {
  configure: {
    content: {
      logParserRules: {
        ruleFormats: [{ type: 'fOO', matches: 'result', name: 'abc' }, { type: 'fOO2', matches: 'result2', name: 'def' }],
        selectedParserRuleIndex: 0,
        deleteRuleStatus: 'error',
        selectedFormat: null,
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
        ]
      }
    }
  }
};

const stateInit = {
  configure: {
    content: {
      logParserRules: {
        deleteRuleStatus: 'wait'
      }
    }
  }
};

const filteredRule = [
  {
    name: 'foo2',
    pattern: {
      format: 'Foo2'
    },
    outOfBox: false
  }
];

const selectedFormat = { type: 'fOO', matches: 'result', name: 'abc' };

test('Test Booleans hasRuleFormats', function(assert) {
  assert.equal(hasRuleFormats(state), true, 'The formats are loaded and hasRuleFormats is true');
  assert.equal(hasRuleFormats(stateInit), false, 'The formats are not loaded and hasRuleFormats is false');
});
test('Test Booleans hasSelectedParserRule', function(assert) {
  assert.equal(hasSelectedParserRule(state), true, 'A parser rule is selected and hasSelectedParserRule is true');
  assert.equal(hasSelectedParserRule(stateInit), false, 'A parser rule is not selected and hasSelectedParserRule is false');
});
test('filterDeletedRule by selectedParserRuleIndex', function(assert) {
  assert.deepEqual(filterDeletedRule(state), filteredRule, 'The rule with index === 0 was filtered');
});
test('isDeletingParserRule wait', function(assert) {
  assert.equal(isDeletingParserRule(stateInit), true, 'waiting for delete confirmation');
});
test('isDeletingParserRuleError error', function(assert) {
  assert.equal(isDeletingParserRuleError(state), true, 'error getting delete confirmation');
});
test('isDeletingParserRule wait', function(assert) {
  assert.equal(isDeletingParserRule(state), false, 'waiting for delete no confirmation');
});
test('isDeletingParserRuleError error', function(assert) {
  assert.equal(isDeletingParserRuleError(stateInit), false, 'no error getting delete confirmation');
});

test('isOotb', function(assert) {
  assert.equal(isOotb(state), true, 'is ootb');
});

test('selectedParserRuleFormat with _selectedFormat', function(assert) {
  assert.deepEqual(selectedParserRuleFormat(state), selectedFormat, 'OK');
});
test('selectedParserRuleFormat without _selectedFormat', function(assert) {
  assert.deepEqual(selectedParserRuleFormat(stateNoSelectedFormat), selectedFormat, 'OK');
});

import { module, test } from 'qunit';
import {
  hasRuleFormats,
  hasSelectedParserRule,
  filterDeletedRule,
  parserRuleMatches,
  isDeletingParserRule,
  isDeletingParserRuleError,
  isOotb
} from 'configure/reducers/content/log-parser-rules/selectors';

module('Unit | Selectors | log-parser-rules');

const state = {
  configure: {
    content: {
      logParserRules: {
        ruleFormats: [{ type: 'fOO', matches: 'result' }, { type: 'fOO2', matches: 'result2' }],
        selectedParserRuleIndex: 0,
        deleteRuleStatus: 'error',
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
test('parserRuleMatches toLowerCase', function(assert) {
  assert.equal(parserRuleMatches(state), 'result', 'parserRuleMatches Foo and fOO');
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

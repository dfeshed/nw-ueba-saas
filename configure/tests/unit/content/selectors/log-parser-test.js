import { module, test } from 'qunit';
import {
  hasRuleFormats,
  hasSelectedParserRule
} from 'configure/reducers/content/log-parser-rules/selectors';

module('Unit | Selectors | log-parser-rules');

const state = {
  configure: {
    content: {
      logParserRules: {
        ruleFormats: [1, 2, 3],
        selectedParserRuleIndex: 0,
        parserRules: [
          {
            name: 'foo'
          }
        ]
      }
    }
  }
};

const stateInit = {
  configure: {
    content: {
      logParserRules: {}
    }
  }
};

test('Test Booleans hasRuleFormats', function(assert) {
  assert.equal(hasRuleFormats(state), true, 'The formats are loaded and hasRuleFormats is true');
  assert.equal(hasRuleFormats(stateInit), false, 'The formats are not loaded and hasRuleFormats is false');
});
test('Test Booleans hasSelectedParserRule', function(assert) {
  assert.equal(hasSelectedParserRule(state), true, 'A parser rule is selected and hasSelectedParserRule is true');
  assert.equal(hasSelectedParserRule(stateInit), false, 'A parser rule is not selected and hasSelectedParserRule is false');
});
import reselect from 'reselect';

const { createSelector } = reselect;

const _parserRulesState = (state) => state.configure.content.logParserRules;
const _ruleFormats = (state) => _parserRulesState(state).ruleFormats;
export const logParsers = (state) => _parserRulesState(state).logParsers;
export const parserRules = (state) => _parserRulesState(state).parserRules;
export const selectedLogParserIndex = (state) => _parserRulesState(state).selectedLogParserIndex;
export const selectedParserRuleIndex = (state) => _parserRulesState(state).selectedParserRuleIndex;

export const isLoadingLogParser = createSelector(
  _parserRulesState,
  (parserRulesState) => parserRulesState.logParsersStatus === 'wait'
);

export const isLoadingParserRules = createSelector(
  _parserRulesState,
  (parserRulesState) => parserRulesState.parserRulesStatus === 'wait'
);

export const isLoadingLogParserError = createSelector(
  _parserRulesState,
  (parserRulesState) => parserRulesState.logParsersStatus === 'error'
);

export const isLoadingParserRulesError = createSelector(
  _parserRulesState,
  (parserRulesState) => parserRulesState.parserRulesStatus === 'error'
);

export const selectedLogParserName = createSelector(
  logParsers,
  selectedLogParserIndex,
  (allLogs, index) => {
    if (allLogs && allLogs[index]) {
      return allLogs[index].name;
    } else {
      return '';
    }
  }
);

export const selectedParserRuleName = createSelector(
  parserRules,
  selectedParserRuleIndex,
  (rules, index) => {
    if (rules && rules[index]) {
      return rules[index].name;
    } else {
      return '';
    }
  }
);

const _selectedParserRule = createSelector(
  parserRules,
  selectedParserRuleIndex,
  (rules, index) => {
    if (rules) {
      return rules[index];
    }
  }
);

export const parserRuleRegex = createSelector(
  _selectedParserRule,
  _ruleFormats,
  (selectedRule, formats) => {
    if (selectedRule) {
      const frmt = selectedRule.pattern.format;
      if (frmt) {
        return formats.filter((format) => format.type === frmt.toLowerCase())[0].pattern;
      } else {
        return selectedRule.pattern.regex;
      }
    }
  }
);

export const parserRuleMatches = createSelector(
  _selectedParserRule,
  _ruleFormats,
  (selectedRule, formats) => {
    if (selectedRule) {
      const frmt = selectedRule.pattern.format;
      if (frmt) {
        return formats.filter((format) => format.type === frmt.toLowerCase())[0].matches;
      }
    }
  }
);
// this is only a place holder for UI drop down
export const parserRuleValues = createSelector(
  _ruleFormats,
  (formats) => {
    return formats.map((format) => format.type);
  }
);

export const parserRuleTokens = createSelector(
  _selectedParserRule,
  (selectedRule) => {
    if (selectedRule) {
      return selectedRule.literals;
    }
  }
);

export const parserRuleType = createSelector(
  _selectedParserRule,
  (selectedRule) => {
    if (selectedRule) {
      return selectedRule.pattern.format;
    }
  }
);

export const parserRuleMeta = createSelector(
  _selectedParserRule,
  (selectedRule) => {
    if (selectedRule) {
      return selectedRule.pattern.captures;
    }
  }
);
export const hasSelectedParserRule = createSelector(
  selectedParserRuleIndex,
  (index) => {
    return index !== -1;
  }
);
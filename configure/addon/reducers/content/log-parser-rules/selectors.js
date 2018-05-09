import reselect from 'reselect';

const { createSelector } = reselect;

const _parserRulesState = (state) => state.configure.content.logParserRules;
const _ruleFormats = (state) => _parserRulesState(state).ruleFormats;
export const logParsers = (state) => _parserRulesState(state).logParsers;
export const parserRules = (state) => _parserRulesState(state).parserRules;
export const clickedLogParserIndex = (state) => _parserRulesState(state).clickedLogParserIndex;
export const clickedParserRuleIndex = (state) => _parserRulesState(state).clickedParserRuleIndex;

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

export const firstLogParserName = createSelector(
  logParsers,
  (allLogs) => {
    if (allLogs && allLogs[0]) {
      return allLogs[0].name;
    } else {
      return '';
    }
  }
);

export const firstParserRuleName = createSelector(
  parserRules,
  (rules) => {
    if (rules && rules[0]) {
      return rules[0].name;
    } else {
      return '';
    }
  }
);

export const selectedLogParserName = createSelector(
  logParsers,
  clickedLogParserIndex,
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
  clickedParserRuleIndex,
  (rules, index) => {
    if (rules && rules[index]) {
      return rules[index].name;
    } else {
      return '';
    }
  }
);

export const selectedParserRule = createSelector(
  parserRules,
  selectedParserRuleName,
  (rules, selectedRule) => {
    if (rules) {
      return rules.filter((rule) => rule.name === selectedRule)[0];
    } else {
      return '';
    }
  }
);

export const parserRuleRegex = createSelector(
  selectedParserRule,
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
  selectedParserRule,
  _ruleFormats,
  (selectedRule, formats) => {
    if (selectedRule) {
      const frmt = selectedRule.pattern.format;
      if (frmt) {
        return formats.filter((format) => format.type === frmt.toLowerCase())[0].matches;
      } else {
        return '';
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
  selectedParserRule,
  (selectedRule) => {
    return selectedRule ? selectedRule.literals : '';
  }
);

export const parserRuleType = createSelector(
  selectedParserRule,
  (selectedRule) => {
    if (selectedRule) {
      return selectedRule.pattern.format ? selectedRule.pattern.format : '';
    } else {
      return '';
    }
  }
);

export const parserRuleMeta = createSelector(
  selectedParserRule,
  (selectedRule) => {
    if (selectedRule) {
      return selectedRule.pattern.captures ? selectedRule.pattern.captures : [];
    } else {
      return [];
    }
  }
);
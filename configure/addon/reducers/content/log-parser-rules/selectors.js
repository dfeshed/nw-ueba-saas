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
        return formats.filter((format) => format.type.toLowerCase() === frmt.toLowerCase())[0].pattern;
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
        return formats.filter((format) => format.type.toLowerCase() === frmt.toLowerCase())[0].matches;
      } else {
        return formats[0].matches;
      }
    }
  }
);
// this is only a place holder for UI drop down
export const parserRuleValues = createSelector(
  _ruleFormats,
  (formats) => {
    return formats.map((format) => format.name);
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
  _ruleFormats,
  _selectedParserRule,
  (formats, selectedRule) => {
    if (selectedRule) {
      return selectedRule.pattern.format ? selectedRule.pattern.format : formats[0].type;
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
  selectedParserRuleName,
  (name) => {
    return (name !== '');
  }
);

export const hasRuleFormats = createSelector(
  _ruleFormats,
  (frmts) => {
    return !!(frmts && frmts[0]);
  }
);

export const filterDeletedRule = createSelector(
  parserRules,
  selectedParserRuleIndex,
  (rules, selectedIndex) => {
    return rules.filter((rule, index) => index !== selectedIndex);
  }
);

export const isDeletingParserRule = createSelector(
  _parserRulesState,
  (parserRulesState) => parserRulesState.deleteRuleStatus === 'wait'
);

export const isDeletingParserRuleError = createSelector(
  _parserRulesState,
  (parserRulesState) => parserRulesState.deleteRuleStatus === 'error'
);
export const isSavingParserRule = createSelector(
  _parserRulesState,
  (parserRulesState) => parserRulesState.saveRuleStatus === 'wait'
);

export const isSavingParserRuleError = createSelector(
  _parserRulesState,
  (parserRulesState) => parserRulesState.saveRuleStatus === 'error'
);

export const isOotb = createSelector(
  _selectedParserRule,
  (rule) => {
    if (rule) {
      return !!(rule.outOfBox);
    }
  }
);
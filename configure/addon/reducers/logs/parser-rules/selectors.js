import reselect from 'reselect';

const { createSelector } = reselect;

const parserRulesState = (state) => state.configure.logs.parserRules;

export const findAllLogParsers = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.parserRules
);

export const getRules = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.rules
);

export const getSelectedLogName = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.selectedLogName
);

export const getSelectedRuleId = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.selectedRuleId
);

export const getFirstLogName = createSelector(
  findAllLogParsers,
  (allLogs) => {
    if (allLogs && allLogs[0]) {
      return allLogs[0].name;
    } else {
      return '';
    }
  }
);

export const getFirstRuleName = createSelector(
  getRules,
  (rules) => {
    if (rules && rules[0]) {
      return rules[0].name;
    } else {
      return '';
    }
  }
);

const _getFormats = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.formats
);

export const getTokens = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.theTokens
);

export const isLoading = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.parserRulesStatus === 'wait'
);

export const isLoadingRules = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.rulesStatus === 'wait'
);

export const isLoadingError = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.parserRulesStatus === 'error'
);

export const isLoadingRulesError = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.rulesStatus === 'error'
);

export const getClickedLogIndex = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.clickedLogIndex
);

export const getClickedRuleIndex = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.clickedRuleIndex
);


export const getSelectedRule = createSelector(
  getRules,
  getSelectedRuleId,
  (rules, selectedRuleId) => {
    if (rules) {
      return rules.filter((rule) => rule.name === selectedRuleId)[0];
    } else {
      return '';
    }
  }
);

export const getRuleRegex = createSelector(
  getSelectedRule,
  _getFormats,
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

export const getRuleMatches = createSelector(
  getSelectedRule,
  _getFormats,
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
export const getRuleValues = createSelector(
  _getFormats,
  (formats) => {
    return formats.map((format) => format.type);
  }
);

export const getRuleTokens = createSelector(
  getSelectedRule,
  (selectedRule) => {
    return selectedRule ? selectedRule.literals : '';
  }
);

export const getType = createSelector(
  getSelectedRule,
  (selectedRule) => {
    if (selectedRule) {
      return selectedRule.pattern.format ? selectedRule.pattern.format : '';
    } else {
      return '';
    }
  }
);

export const getMeta = createSelector(
  getSelectedRule,
  (selectedRule) => {
    if (selectedRule) {
      return selectedRule.pattern.captures ? selectedRule.pattern.captures : [];
    } else {
      return [];
    }
  }
);
import reselect from 'reselect';

const { createSelector } = reselect;

const parserRulesState = (state) => state.configure.logs.parserRules;

export const findAllLogParsers = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.parserRules
);

export const getSelectedLogName = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.selectedParserId
);

export const getSelectedRuleId = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.selectedRuleId
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

export const getClickedRule = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.clickedRule
);

export const getClickedLog = createSelector(
  parserRulesState,
  (parserRulesState) => parserRulesState.clickedLog
);

export const getRules = createSelector(
  findAllLogParsers,
  getSelectedLogName,
  (logParsers, logName) => {
    const selectedParser = logParsers.filter((log) => log.name === logName);
    if (selectedParser[0]) {
      if (selectedParser[0].parserFiles[1]) {
        return selectedParser[0].parserFiles[0].parserRules.concat(selectedParser[0].parserFiles[1].parserRules);
      } else {
        return selectedParser[0].parserFiles[0].parserRules;
      }
    } else {
      return [];
    }
  }
);

export const getSelectedRule = createSelector(
  getRules,
  getSelectedRuleId,
  (rules, selectedRuleId) => {
    return rules.filter((rule) => rule.name === selectedRuleId)[0];
  }
);

export const getSelectedRuleRegex = createSelector(
  getSelectedRule,
  _getFormats,
  (selectedRule, formats) => {
    if (selectedRule) {
      if (selectedRule.pattern.format) {
        return formats.filter((format) => format.type === selectedRule.pattern.format)[0].pattern;
      } else {
        return selectedRule.pattern.regex;
      }
    }
  }
);

export const getSelectedRuleTokens = createSelector(
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
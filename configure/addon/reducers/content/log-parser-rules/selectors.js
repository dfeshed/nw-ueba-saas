import reselect from 'reselect';

const { createSelector } = reselect;

const _parserRulesState = (state) => state.configure.content.logParserRules;
const _ruleFormats = (state) => _parserRulesState(state).ruleFormats;
const _selectedFormat = (state) => _parserRulesState(state).selectedFormat;
export const logParsers = (state) => _parserRulesState(state).logParsers;
export const parserRules = (state) => _parserRulesState(state).parserRules;
export const selectedLogParserIndex = (state) => _parserRulesState(state).selectedLogParserIndex;
export const selectedParserRuleIndex = (state) => _parserRulesState(state).selectedParserRuleIndex;
export const deviceTypes = (state) => _parserRulesState(state).deviceTypes;
export const deviceClasses = (state) => _parserRulesState(state).deviceClasses;

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
  (selectedRule) => {
    if (selectedRule) {
      return selectedRule.pattern.regex ? selectedRule.pattern.regex : '';
    }
  }
);

export const parserRuleFormatNames = createSelector(
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

export const hasDeployableRules = createSelector(
  parserRules,
  (rules) => {
    if (rules) {
      let index;
      for (index = 0; index < rules.length; ++index) {
        if (!rules[index].outOfBox) {
          return true;
        }
      }
    }
    return false;
  }
);

export const selectedParserRuleFormat = createSelector(
  _selectedParserRule,
  _selectedFormat,
  _ruleFormats,
  (selectedParserRule, _selectedFormat, ruleFormats) => {
    if (_selectedFormat) {
      return ruleFormats.filter((format) => format.name === _selectedFormat)[0];
    } else if (selectedParserRule) {
      const frmt = selectedParserRule.pattern.format ? selectedParserRule.pattern.format : 'regex';
      return ruleFormats.filter((format) => format.type.toLowerCase() === frmt.toLowerCase())[0];
    }
  }
);

// Returns all device types that are not already represented in the set of log parsers
export const availableDeviceTypes = createSelector(
  deviceTypes, logParsers,
  (deviceTypes, logParsers) => {
    const logParserNames = logParsers.map((parser) => parser.name);
    return deviceTypes.filter((deviceType) => !logParserNames.includes(deviceType.name));
  }
);
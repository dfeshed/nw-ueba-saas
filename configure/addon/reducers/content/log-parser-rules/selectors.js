import reselect from 'reselect';

const { createSelector } = reselect;

const _parserRulesState = (state) => state.configure.content.logParserRules;

export const ruleFormats = (state) => _parserRulesState(state).ruleFormats;
export const logParsers = (state) => _parserRulesState(state).logParsers;
export const parserRules = (state) => _parserRulesState(state).parserRules;
export const selectedLogParserIndex = (state) => _parserRulesState(state).selectedLogParserIndex;
export const selectedParserRuleIndex = (state) => _parserRulesState(state).selectedParserRuleIndex;
export const deviceTypes = (state) => _parserRulesState(state).deviceTypes;
export const deviceClasses = (state) => _parserRulesState(state).deviceClasses;
export const isTransactionUnderway = (state) => _parserRulesState(state).isTransactionUnderway;
export const sampleLogs = (state) => _parserRulesState(state).sampleLogs;
export const sampleLogsStatus = (state) => _parserRulesState(state).sampleLogsStatus;

export const selectedParserRule = createSelector(
  parserRules,
  selectedParserRuleIndex,
  (rules, index) => {
    if (rules) {
      return rules[index];
    }
  }
);

export const isHighlighting = createSelector(
  _parserRulesState,
  (parserRulesState) => parserRulesState.sampleLogsStatus === 'wait'
);

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

export const selectedLogParser = createSelector(
  logParsers,
  selectedLogParserIndex,
  (parsers, index) => parsers[index]
);

export const selectedLogParserName = createSelector(
  selectedLogParser,
  (selectedParser) => selectedParser ? selectedParser.name : ''
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

export const parserRuleMeta = createSelector(
  selectedParserRule,
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
  ruleFormats,
  (ruleFormats) => {
    return ruleFormats && !!ruleFormats.length;
  }
);

// TODO: Can we remove this and move logic elsewhere. Seems only used in the action creator for deleting rule, not in a component
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

export const isParserRuleOutOfBox = createSelector(
  selectedParserRule,
  (rule) => {
    if (rule) {
      return !!(rule.outOfBox);
    }
  }
);

export const hasDeployableRules = createSelector(
  selectedLogParser,
  parserRules,
  (parser, rules) => {
    if (rules) {
      let index;
      for (index = 0; index < rules.length; ++index) {
        if (!rules[index].outOfBox) {
          return true;
        }
      }
    }
    return parser && parser.dirty && parser.deployed;
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

// Returns the sample logs which may have been highlighted (i.e., they include html spans around certain words or groups
// of words). Each span includes a class name, either "highlight_literal_[RULE_NAME]" or "highlight_capture_[RULE_NAME]", where
// the rule name portion of the class name has had all spaces removed. Here we want to convert the text/html so that if
// the currently selected rule name is in the class name, we replace it with an "is-selected" class, otherwise strip out the
// rule name altogether.
export const highlightedLogs = createSelector(
  sampleLogs,
  selectedParserRuleName,
  (sampleLogs = '', selectedRuleName) => {
    let logs = '';
    const normalizedRuleName = selectedRuleName.replace(/\s/g, '');
    if (normalizedRuleName) {
      logs = sampleLogs.replace(new RegExp(`_${normalizedRuleName}'`, 'g'), '_is-selected\'');
    }
    logs = logs.replace(/highlight_literal_/g, 'highlight-literal ').replace(/highlight_capture_/g, 'highlight-capture ');
    return logs;
  }
);
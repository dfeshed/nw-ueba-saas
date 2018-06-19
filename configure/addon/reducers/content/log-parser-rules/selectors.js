import reselect from 'reselect';
import _ from 'lodash';
import { hasInvalidCaptures } from 'configure/utils/reconcile-regex-captures';

const { createSelector } = reselect;

const _hasInvalidCaptures = ({ pattern: { regex, captures } }) => {
  return hasInvalidCaptures(regex, captures);
};

const _isRuleInvalid = (rule) => {
  let isInvalid = false;
  if (!rule.literals || !rule.literals.length) { // A rule must have at least one token
    isInvalid = true;
  } else if (!rule.pattern.captures || !rule.pattern.captures.length) { // A rule must have at least one meta capture
    isInvalid = true;
  } else if (!rule.pattern.format && !rule.pattern.regex) { // If a rule is a regex rule, it must have a regex value
    isInvalid = true;
  } else if (!rule.pattern.format && _hasInvalidCaptures(rule)) { // If the rule's meta capture group count is more than the regex contains
    isInvalid = true;
  }
  return isInvalid;
};

const _parserRulesState = (state) => state.configure.content.logParserRules;

export const ruleFormats = (state) => _parserRulesState(state).ruleFormats;
export const logParsers = (state) => _parserRulesState(state).logParsers;
export const parserRules = (state) => _parserRulesState(state).parserRules;
const _parserRulesOriginal = (state) => _parserRulesState(state).parserRulesOriginal;
const _deletedRules = (state) => _parserRulesState(state)._deletedRules;
export const selectedLogParserIndex = (state) => _parserRulesState(state).selectedLogParserIndex;
export const selectedParserRuleIndex = (state) => _parserRulesState(state).selectedParserRuleIndex;
export const deviceTypes = (state) => _parserRulesState(state).deviceTypes;
export const deviceClasses = (state) => _parserRulesState(state).deviceClasses;
export const metas = (state) => _parserRulesState(state).metas;
export const isTransactionUnderway = (state) => _parserRulesState(state).isTransactionUnderway;
export const sampleLogs = (state) => _parserRulesState(state).sampleLogs;
export const sampleLogsStatus = (state) => _parserRulesState(state).sampleLogsStatus;


// Compares (deeply) the copy of the original parser rules with the current parser rules to determine if any
// changes have been made
export const hasRuleChanges = createSelector(
  parserRules, _parserRulesOriginal,
  (parserRules, originalParserRules) => {
    return !_.isEqual(parserRules, originalParserRules);
  }
);

// Returns the currently selected parser rule
export const selectedParserRule = createSelector(
  parserRules,
  selectedParserRuleIndex,
  (rules, index) => {
    if (rules) {
      return rules[index];
    }
  }
);

// Returns an array of rule names for rules that have validation errors for the currently selected parser
// A rule is invalid if it has no tokens (literals), no captures, or is regex but without a regex value
export const invalidRules = createSelector(
  parserRules,
  (rules) => rules.reduce((accumulator, rule) => {
    if (_isRuleInvalid(rule)) {
      accumulator.push(rule.name);
    }
    return accumulator;
  }, [])
);

// Returns true if any of the rules for the currently selected parser are invalid
export const hasInvalidRules = createSelector(
  invalidRules,
  parserRules,
  (invalidRules, rules) => {
    // there are invalid rules if there are no rules at all, or if any one rule is invalid
    return (!rules.length && !_deletedRules.length) || !!invalidRules.length;
  }
);

// Returns the set of current parser rules that are valid
export const validRules = createSelector(
  parserRules,
  hasInvalidRules,
  (rules, hasInvalidRules) => {
    return hasInvalidRules ? rules.filter((rule) => !_isRuleInvalid(rule)) : rules;
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

// Returns true if parser's rule can be deployed to the log decoder
export const hasDeployableRules = createSelector(
  [selectedLogParser, parserRules, hasInvalidRules, hasRuleChanges],
  (parser, rules, hasInvalidRules, hasRuleChanges) => {
    if (hasInvalidRules || hasRuleChanges) { // unsaved changes or invalid rules do not allow deployment
      return false;
    }
    if (rules) {
      let index;
      for (index = 0; index < rules.length; ++index) {
        if (!rules[index].outOfBox) { // a parser with at least one non-out-of-box rule can be deployed
          return true;
        }
      }
    }
    return parser && parser.dirty && parser.deployed; // a dirty but previously deployed parser can be redeployed
  }
);

// Returns true if the rules can be saved (i.e., there are edits to save and no invalid rules)
export const hasSaveableRules = createSelector(
  hasRuleChanges, hasInvalidRules,
  (hasChanges, hasInvalidRules) => {
    return hasChanges && !hasInvalidRules;
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
    let logs = sampleLogs;
    const normalizedRuleName = selectedRuleName.replace(/\s/g, '');
    if (normalizedRuleName) {
      logs = sampleLogs.replace(new RegExp(`_${normalizedRuleName}'`, 'g'), '_is-selected\'');
    }
    logs = logs.replace(/highlight_literal_/g, 'highlight-literal ').replace(/highlight_capture_/g, 'highlight-capture ');
    return logs;
  }
);

const _highlightingMatch = /highlight_[capture|literal]+_(.+?)'/g;
// Returns the rule names that have highlight matches in the sample logs
export const highlightedRuleNames = createSelector(
  sampleLogs,
  parserRules,
  (sampleLogs, parserRules) => {
    let match;
    const matchedRules = [];
    do {
      match = _highlightingMatch.exec(sampleLogs);
      if (match) {
        const highlightedRule = parserRules.find((rule) => rule.name.replace(/\s/g, '') === match[1]);
        if (highlightedRule) {
          matchedRules.push(highlightedRule.name); // push the first group capture in the matched rules array
        }
      }
    } while (match);
    return _.uniq(matchedRules);
  }
);
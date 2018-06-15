import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  selectedLogParserName,
  selectedLogParser,
  hasDeployableRules,
  hasSaveableRules,
  hasRuleChanges
} from 'configure/reducers/content/log-parser-rules/selectors';
import {
  discardRuleChanges,
  saveParserRule,
  deployLogParser } from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  selectedLogParserName: selectedLogParserName(state),
  selectedLogParser: selectedLogParser(state),
  hasNoDeployableRules: !hasDeployableRules(state),
  hasSaveableRules: hasSaveableRules(state),
  hasRuleChanges: hasRuleChanges(state)
});

const dispatchToActions = {
  deployLogParser,
  discardRuleChanges,
  saveParserRule
};

const SaveResetRule = Component.extend({
  classNames: ['save-reset-rule']
});

export default connect(stateToComputed, dispatchToActions)(SaveResetRule);
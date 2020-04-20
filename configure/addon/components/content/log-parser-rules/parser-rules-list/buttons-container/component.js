import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import computed from 'ember-computed-decorators';
import {
  parserRules,
  selectedLogParserName,
  hasSelectedParserRule,
  selectedParserRuleName,
  isParserRuleOutOfBox
} from 'configure/reducers/content/log-parser-rules/selectors';
import { deleteParserRule } from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  parserRules: parserRules(state),
  selectedLogParserName: selectedLogParserName(state),
  hasSelectedParserRule: hasSelectedParserRule(state),
  selectedParserRuleName: selectedParserRuleName(state),
  isParserRuleOutOfBox: isParserRuleOutOfBox(state)
});

const dispatchToActions = {
  deleteParserRule
};

const LogParserRulesToolbar = Component.extend({
  classNames: ['log-parser-rules-toolbar', 'buttons-container'],
  newRuleName: '',
  accessControl: inject(),
  @computed('isParserRuleOutOfBox', 'hasSelectedParserRule', 'accessControl.canManageLogParsers')
  cannotDeleteRule(isParserRuleOutOfBox, hasSelectedParserRule, canManageLogParsers) {
    return !canManageLogParsers || isParserRuleOutOfBox || !hasSelectedParserRule;
  }
});
export default connect(stateToComputed, dispatchToActions)(LogParserRulesToolbar);

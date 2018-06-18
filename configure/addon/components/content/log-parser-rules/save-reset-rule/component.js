import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
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

const DeploySaveReset = Component.extend({
  classNames: ['save-reset-rule'],
  accessControl: service(),

  @computed('hasNoDeployableRules', 'selectedLogParser', 'accessControl.canManageLogParsers')
  cannotDeploy(hasNoDeployableRules, selectedLogParser, canManageLogParsers) {
    return !canManageLogParsers || !selectedLogParser || hasNoDeployableRules;
  },

  @computed('hasSaveableRules', 'accessControl.canManageLogParsers')
  cannotSave(hasSaveableRules, canManageLogParsers) {
    return !canManageLogParsers || !hasSaveableRules;
  },

  @computed('hasRuleChanges', 'accessControl.canManageLogParsers')
  cannotDiscardChanges(hasRuleChanges, canManageLogParsers) {
    return !canManageLogParsers || !hasRuleChanges;
  }
});

export default connect(stateToComputed, dispatchToActions)(DeploySaveReset);
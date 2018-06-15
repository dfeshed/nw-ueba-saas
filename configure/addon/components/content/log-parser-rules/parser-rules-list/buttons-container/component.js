import Component from '@ember/component';
import { isPresent } from '@ember/utils';
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
import { deleteParserRule, addNewParserRule } from 'configure/actions/creators/content/log-parser-rule-creators';

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

const VALID_RULENAME_REGEX = /^[^<>]{1,64}$/;

const LogParserRulesToolbar = Component.extend({
  classNames: ['log-parser-rules-toolbar', 'buttons-container'],
  newRuleName: '',
  eventBus: inject(),
  redux: inject(),

  @computed('newRuleName', 'selectedLogParserName', 'parserRules')
  matchesExistingRuleName(name, parser, rules) {
    if (isPresent(name) && isPresent(parser) && isPresent(rules) &&
      (rules.filter((rule) => (rule.name.toLowerCase() === name.toLowerCase())).length > 0)) {
      return true;
    }
    return false;
  },

  @computed('newRuleName', 'selectedLogParserName', 'parserRules')
  inValidRuleName(name, parser, rules) {
    if (!isPresent(name) || !isPresent(parser) || !VALID_RULENAME_REGEX.test(name) ||
      (isPresent(rules) && (rules.filter((rule) => (rule.name.toLowerCase() === name.toLowerCase())).length > 0))) {
      return true;
    }
    return false;
  },

  actions: {
    addNewParserRule(name) {
      const redux = this.get('redux');
      this.get('eventBus').trigger('rsa-application-modal-close-addNewRule');
      redux.dispatch(addNewParserRule(name));
      this.set('newRuleName', '');
    },
    handleCloseAddRule() {
      this.set('newRuleName', '');
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(LogParserRulesToolbar);

import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  selectedLogParserName
} from 'configure/reducers/content/log-parser-rules/selectors';
import { fetchParserRules, saveParserRule } from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  selectedLogParserName: selectedLogParserName(state)
});

const dispatchToActions = {
  fetchParserRules,
  saveParserRule
};

const SaveResetRule = Component.extend({
  classNames: ['save-reset-rule']
});
export default connect(stateToComputed, dispatchToActions)(SaveResetRule);
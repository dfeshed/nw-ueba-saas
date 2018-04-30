import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getRuleTokens } from 'configure/reducers/logs/parser-rules/selectors';

const stateToComputed = (state) => ({
  ruleTokens: getRuleTokens(state)
});

const TokenMatching = Component.extend({
  classNames: ['token-matching']
});
export default connect(stateToComputed)(TokenMatching);

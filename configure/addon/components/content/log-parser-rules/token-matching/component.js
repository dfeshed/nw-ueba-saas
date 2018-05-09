import Component from '@ember/component';
import { connect } from 'ember-redux';
import { parserRuleTokens } from 'configure/reducers/content/log-parser-rules/selectors';

const stateToComputed = (state) => ({
  parserRuleTokens: parserRuleTokens(state)
});

const TokenMatching = Component.extend({
  classNames: ['token-matching']
});
export default connect(stateToComputed)(TokenMatching);

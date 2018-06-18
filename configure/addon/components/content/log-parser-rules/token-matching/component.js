import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedParserRule } from 'configure/reducers/content/log-parser-rules/selectors';
import { updateSelectedRule } from 'configure/actions/creators/content/log-parser-rule-creators';
import { next } from '@ember/runloop';
import computed, { not, empty } from 'ember-computed-decorators';

const stateToComputed = (state) => {
  return {
    rule: selectedParserRule(state)
  };
};

const dispatchToActions = {
  updateSelectedRule
};

const TokenMatching = Component.extend({
  tagName: 'td',
  classNames: ['token'],
  newToken: '',

  @computed('rule')
  tokens(rule) {
    return rule.literals || [];
  },

  @computed('newToken', 'tokens')
  isNewTokenInvalid(newToken, currentTokens) {
    return !newToken || !newToken.trim() || !!currentTokens.findBy('value', newToken);
  },

  @not('rule.outOfBox') isEditable: true,

  @empty('tokens') hasNoTokens: false,

  actions: {
    addToken() {
      const { rule, newToken, tokens } = this.getProperties('rule', 'newToken', 'tokens');
      const updatedRule = {
        ...rule,
        literals: [{ value: newToken }, ...tokens]
      };
      this.send('updateSelectedRule', updatedRule);
      next(this, () => this.set('newToken', ''));
    },

    editToken(originalToken, tokenIndex, event) {
      const updatedToken = event.target.value;
      // If the token is only whitespace, replace with the original/previous token
      if (updatedToken.trim() === '') {
        event.target.value = originalToken;
      } else if (updatedToken !== originalToken) {
        const { rule, tokens } = this.getProperties('rule', 'tokens');
        const updatedRule = rule.set('literals', tokens.map((token, idx) => idx === tokenIndex ? { value: updatedToken } : token));
        this.send('updateSelectedRule', updatedRule);
      }
    },

    deleteToken(tokenToDelete) {
      const { rule, tokens } = this.getProperties('rule', 'tokens');
      const updatedRule = rule.set('literals', tokens.filter((token) => token.value !== tokenToDelete.value));
      this.send('updateSelectedRule', updatedRule);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(TokenMatching);

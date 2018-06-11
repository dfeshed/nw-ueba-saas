import Component from '@ember/component';
import { connect } from 'ember-redux';
import { parserRuleTokens } from 'configure/reducers/content/log-parser-rules/selectors';
import { addRuleToken, deleteRuleToken, editRuleToken } from 'configure/actions/creators/content/log-parser-rule-creators';
import { next } from '@ember/runloop';

const stateToComputed = (state) => {
  return {
    parserRuleTokens: parserRuleTokens(state)
  };
};

const dispatchToActions = {
  deleteRuleToken,
  addRuleToken,
  editRuleToken
};

const TokenMatching = Component.extend({
  classNames: ['token-matching'],
  newToken: '',
  actions: {
    addToken() {
      const token = this.get('newToken');
      this.send('addRuleToken', token);
      next(this, () => this.set('newToken', ''));
    },
    editToken(originalToken, index, event) {
      const token = event.target.value;
      if (token !== originalToken) {
        this.send('editRuleToken', token, index);
      }
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(TokenMatching);

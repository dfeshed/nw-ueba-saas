import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  parserRuleRegex,
  parserRuleType,
  parserRuleMatches,
  parserRuleValues
} from 'configure/reducers/content/log-parser-rules/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  parserRuleRegex: parserRuleRegex(state),
  parserRuleType: parserRuleType(state),
  parserRuleMatches: parserRuleMatches(state),
  parserRuleValues: parserRuleValues(state)
});

const ValueMatching = Component.extend({
  classNames: ['value-matching'],
  itemSelected: null,
  actions: {
    setSelect(item) {
      this.set('itemSelected', item);
    }
  },
  @computed('itemSelected')
  isRegexSelected(itemSelected) {
    return itemSelected === 'Regex Pattern';
  }
});
export default connect(stateToComputed)(ValueMatching);

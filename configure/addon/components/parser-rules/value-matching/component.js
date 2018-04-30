import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getRuleRegex, getType, getRuleMatches, getRuleValues } from 'configure/reducers/logs/parser-rules/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  ruleRegex: getRuleRegex(state),
  ruleType: getType(state),
  ruleMatches: getRuleMatches(state),
  ruleValues: getRuleValues(state)
});

const ValueMatching = Component.extend({
  classNames: ['value-matching'],
  itemSelected: null,
  actions: {
    setSelect(item) {
      this.set('theMatchVal', item);
      this.set('itemSelected', item);
    }
  },
  @computed('itemSelected')
  isRegexSelected(itemSelected) {
    return itemSelected !== 'regex';
  }
});
export default connect(stateToComputed)(ValueMatching);

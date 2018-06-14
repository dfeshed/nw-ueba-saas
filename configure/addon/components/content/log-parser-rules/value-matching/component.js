import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedParserRule, ruleFormats } from 'configure/reducers/content/log-parser-rules/selectors';
import { updateSelectedRule } from 'configure/actions/creators/content/log-parser-rule-creators';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  rule: selectedParserRule(state),
  formatOptions: ruleFormats(state)
});

const dispatchToActions = {
  updateSelectedRule
};

const ValueMatching = Component.extend({
  classNames: ['value-matching'],

  @computed('rule.pattern.format')
  format(format) {
    return (format && format.toLowerCase()) || 'regex';
  },

  @computed('rule.pattern.regex')
  regex(regex) {
    return regex || '';
  },

  @computed('format', 'formatOptions')
  selectedFormat(formatValue, formatOptions) {
    return formatOptions.filter((option) => option.type.toLowerCase() === formatValue)[0];
  },

  @computed('selectedFormat')
  isRegex(selectedFormat) {
    return selectedFormat.type === 'regex';
  },

  actions: {
    handleFormatChange(format) {
      const { rule, regex } = this.getProperties('rule', 'regex');
      const updates = {
        format: format.type === 'regex' ? null : format.type,
        regex: format.type === 'regex' ? regex : null
      };
      const pattern = { ...rule.pattern, ...updates };
      const updatedRule = {
        ...rule,
        pattern
      };
      this.send('updateSelectedRule', updatedRule);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(ValueMatching);

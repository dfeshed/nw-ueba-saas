import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedParserRule, ruleFormats } from 'configure/reducers/content/log-parser-rules/selectors';
import { updateSelectedRule } from 'configure/actions/creators/content/log-parser-rule-creators';
import { hasInvalidCaptures } from 'configure/utils/reconcile-regex-captures';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import _ from 'lodash';

const stateToComputed = (state) => ({
  rule: selectedParserRule(state),
  formatOptions: ruleFormats(state)
});

const dispatchToActions = {
  updateSelectedRule
};

const ValueMatching = Component.extend({
  classNames: ['value-matching'],

  accessControl: service(),

  @computed('rule.pattern.format')
  format(format) {
    return (format && format.toLowerCase()) || 'regex';
  },

  _regex: null,

  @computed('rule')
  regex: {
    get(rule) {
      const regex = rule.pattern.regex || '';
      this.set('_regex', regex);
      return regex;
    },
    set(value) {
      this.set('_regex', value);
      return value;
    }
  },

  @computed('format', 'formatOptions')
  selectedFormat(formatValue, formatOptions) {
    return formatOptions.filter((option) => option.type.toLowerCase() === formatValue)[0];
  },

  @computed('selectedFormat')
  isRegex(selectedFormat) {
    return selectedFormat.type === 'regex';
  },

  @computed('regex')
  hasInvalidRegex(regex) {
    let isInvalid = false;
    if (!regex.trim()) { // an null, undefined, or empty value will return as invalid
      isInvalid = true;
    } else { // try and copile the regex and catch any errors
      try {
        new RegExp(regex);
      } catch (e) {
        isInvalid = true;
      }
    }
    return isInvalid;
  },

  @computed('regex', 'rule.pattern.captures', 'hasInvalidRegex')
  hasMissingCaptures(regex, captures, hasInvalidRegex) {
    // if this regex is valid
    if (!hasInvalidRegex) {
      // check if the capture configuration is valid
      return hasInvalidCaptures(regex, captures);
    }
  },

  actions: {
    handleFormatChange(format) {
      const { rule, regex } = this.getProperties('rule', 'regex');
      const isRegexRule = format.type === 'regex';
      // if the format type is not a regex, then find the full capture (index zero), otherwise maintain all captures
      const captures = isRegexRule ? rule.pattern.captures : [rule.pattern.captures.findBy('index', '0')];
      const pattern = {
        ...rule.pattern,
        format: isRegexRule ? null : format.type,
        regex: isRegexRule ? regex : null,
        captures: _.compact(captures)
      };
      const updatedRule = rule.set('pattern', pattern);
      this.send('updateSelectedRule', updatedRule);
    },

    handleRegexChange() {
      const { rule, regex, hasInvalidRegex, hasMissingCaptures } = this.getProperties('rule', 'regex', 'hasInvalidRegex', 'hasMissingCaptures');
      if (rule.pattern.regex !== regex && !hasInvalidRegex && !hasMissingCaptures) {
        const pattern = { ...rule.pattern, regex };
        const updatedRule = rule.set('pattern', pattern);
        this.set('_regex', null);
        this.send('updateSelectedRule', updatedRule);
      }
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(ValueMatching);

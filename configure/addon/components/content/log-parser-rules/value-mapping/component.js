import Component from '@ember/component';
import { connect } from 'ember-redux';
import { metas, selectedParserRule } from 'configure/reducers/content/log-parser-rules/selectors';
import { updateSelectedRule } from 'configure/actions/creators/content/log-parser-rule-creators';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  metaOptions: metas(state),
  rule: selectedParserRule(state)
});

const dispatchToActions = {
  updateSelectedRule
};

const ValueMapping = Component.extend({
  classNames: ['value-mapping'],

  @computed('rule.pattern.captures', 'metaOptions')
  captures(captures, metaOptions) {
    captures = captures || [];
    return captures.map((capture) => metaOptions.findBy('metaName', capture.key));
  },

  actions: {
    handleMetaChange(selectedIndex, selectedMeta) {
      const rule = this.get('rule');
      const captures = rule.pattern.captures.map((capture, idx) => selectedIndex !== idx ? capture :
      {
        key: selectedMeta.metaName,
        format: selectedMeta.format,
        index: capture.index
      });

      const updatedRule = {
        ...rule,
        pattern: {
          ...rule.pattern,
          captures
        }
      };
      this.send('updateSelectedRule', updatedRule);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ValueMapping);

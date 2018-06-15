import Component from '@ember/component';
import { connect } from 'ember-redux';
import { metas, selectedParserRule } from 'configure/reducers/content/log-parser-rules/selectors';
import { updateSelectedRule } from 'configure/actions/creators/content/log-parser-rule-creators';
import computed, { empty } from 'ember-computed-decorators';
import _ from 'lodash';

// If a rule is based on a regex, there are four capture types. If it is not based on a regex, only
// FULL_CAPTURE is possible.
const captureTypes = ['FULL_CAPTURE', 'FIRST_CAPTURE', 'SECOND_CAPTURE', 'THIRD_CAPTURE'];

const stateToComputed = (state) => ({
  metaOptions: metas(state),
  rule: selectedParserRule(state)
});

const dispatchToActions = {
  updateSelectedRule
};

const MetaCaptures = Component.extend({
  classNames: ['meta-captures'],

  @empty('rule.pattern.captures') hasNoCapturesConfigured: false,

  /**
   * This property is an array of the captures types available for the rule. For example, if a rule is not a regex
   * based rule, then there is only one capture type (full capture), otherwise all four captures are available (full
   * capture, first, second, and third capture)
   * property availableCaptureTypes
   * @private
   * @param format
   * @returns {*}
   */
  @computed('rule.pattern.format')
  availableCaptureTypes(format) {
    const [fullCapture] = captureTypes;
    // If there's no format, this is a regex, and we use all captures, otherwise just full capture
    return !format ? captureTypes : [fullCapture];
  },

  /**
   * A composite of the rule's capture settings which incluedes the meta-option object and the type of capture (e.g,
   * full capture, first capture, etc.
   * @property metaCaptures
   * @private
   * @param availableCaptureTypes
   * @param ruleCaptures
   * @param metaOptions
   * @returns {*}
   */
  @computed('availableCaptureTypes', 'rule.pattern.captures', 'metaOptions')
  metaCaptures(availableCaptureTypes, ruleCaptures, metaOptions) {
    ruleCaptures = ruleCaptures || [];

    return availableCaptureTypes.map((type, index) => {
      const ruleCapture = ruleCaptures.findBy('index', index.toString());
      return {
        metaOption: !ruleCapture ? null : (metaOptions.findBy('metaName', ruleCapture.key) || null),
        captureType: type
      };
    });
  },

  actions: {
    handleMetaChange(selectedIndex, selectedMeta) {
      const { rule, metaCaptures } = this.getProperties('rule', 'metaCaptures');

      // utility func that creates a rule-compiant capture object from a metaOption and an
      // index ("0" = full capture, "1" = first capture, etc)
      const _createCapture = (metaOption, index) => metaOption ? {
        key: metaOption.metaName,
        format: metaOption.format,
        index: index.toString()
      } : null;

      // Produces an updated array of captures ready to be added back into the rule
      const updatedCaptures = metaCaptures.map((capture, index) => {
        const metaOption = index === selectedIndex ? selectedMeta : capture.metaOption;
        return _createCapture(metaOption, index);
      });

      const updatedRule = rule.setIn(['pattern', 'captures'], _.compact(updatedCaptures));
      this.send('updateSelectedRule', updatedRule);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(MetaCaptures);

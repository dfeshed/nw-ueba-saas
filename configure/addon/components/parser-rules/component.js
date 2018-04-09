import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isLoading,
  getSelectedRuleTokens,
  getSelectedRuleRegex,
  getType,
  getMeta,
  getSelectedLogName,
  getSelectedRuleId
} from 'configure/reducers/logs/parser-rules/selectors';

import Ember from 'ember';
const { $ } = Ember;

const stateToComputed = (state) => ({
  isLoading: isLoading(state),
  ruleRegex: getSelectedRuleRegex(state),
  ruleTokens: getSelectedRuleTokens(state),
  ruleType: getType(state),
  ruleMetas: getMeta(state),
  logName: getSelectedLogName(state),
  ruleName: getSelectedRuleId(state)
});

const ParserRules = Component.extend({
  classNames: ['log-parser-rules'],
  didRender() {
    this._super(...arguments);
    // table height to full window on load
    const p = $('.log-parser-rules').position();
    const n = Math.round(p.top) + 60;
    $('.parserContainer').css('height', ($(window).height() - n));
    // table height to full window on window resize
    $(window).resize(function() {
      $('.parserContainer').css('height', ($(window).height() - n));
    });
  }
});
export default connect(stateToComputed)(ParserRules);

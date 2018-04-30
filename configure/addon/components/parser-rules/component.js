import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isLoading,
  getSelectedLogName,
  getSelectedRuleId,
  isLoadingError
} from 'configure/reducers/logs/parser-rules/selectors';

import Ember from 'ember';
const { $ } = Ember;

const stateToComputed = (state) => ({
  isLoading: isLoading(state),
  isLoadingError: isLoadingError(state),
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

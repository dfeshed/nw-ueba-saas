import Component from '@ember/component';
import { connect } from 'ember-redux';
import { and } from 'ember-computed-decorators';
import {
  isLoadingLogParser,
  selectedLogParserName,
  selectedParserRuleName,
  isLoadingLogParserError,
  hasSelectedParserRule,
  hasRuleFormats,
  isTransactionUnderway
} from 'configure/reducers/content/log-parser-rules/selectors';

import Ember from 'ember';
const { $ } = Ember;

const stateToComputed = (state) => ({
  isLoading: isLoadingLogParser(state),
  isLoadingError: isLoadingLogParserError(state),
  logParserName: selectedLogParserName(state),
  parserRuleName: selectedParserRuleName(state),
  hasSelectedParserRule: hasSelectedParserRule(state),
  hasRuleFormats: hasRuleFormats(state),
  isTransactionUnderway: isTransactionUnderway(state)
});

const ParserRules = Component.extend({
  classNames: ['log-parser-rules'],
  classNameBindings: ['isTransactionUnderway:transaction-in-progress'],
  @and('hasSelectedParserRule', 'hasRuleFormats')
  canShowSelectedParserRule: true,
  didRender() {
    this._super(...arguments);
    // table height to full window on load and center are slit in half
    const p = $('.log-parser-rules').position();
    const n = Math.round(p.top) + 100;
    let h = $(window).height() - n;
    $('.parserContainer').css('height', h);
    $('.trTop, .trMessage, .matchingMapping').css('height', h / 2);
    // same thing on  window resize
    $(window).resize(function() {
      h = $(window).height() - n;
      $('.parserContainer').css('height', h);
      $('.trTop, .trMessage').css('height', h / 2);
    });
  },
  didDestroyElement() {
    $(window).unbind('resize');
  }
});
export default connect(stateToComputed)(ParserRules);

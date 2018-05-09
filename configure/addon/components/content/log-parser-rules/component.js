import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  isLoadingLogParser,
  selectedLogParserName,
  selectedParserRuleName,
  isLoadingLogParserError
} from 'configure/reducers/content/log-parser-rules/selectors';

import Ember from 'ember';
const { $ } = Ember;

const stateToComputed = (state) => ({
  isLoading: isLoadingLogParser(state),
  isLoadingError: isLoadingLogParserError(state),
  logParserName: selectedLogParserName(state),
  parserRuleName: selectedParserRuleName(state)
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

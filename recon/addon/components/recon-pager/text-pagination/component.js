import Component from '@ember/component';
import layout from './template';
import { alias } from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import {
  textPageFirst,
  textPagePrevious,
  textPageNext,
  textPageLast
} from 'recon/actions/data-creators';

const stateToComputed = ({ recon: { text } }) => ({
  textPageNumber: text.textPageNumber,
  textPagePrevious: text.canPrevious,
  textPageNext: text.canNext,
  textPageLast: text.canLast
});

const dispatchToActions = {
  textPageFirst,
  textPagePrevious,
  textPageNext,
  textPageLast
};

const reconTextPagination = Component.extend({
  layout,
  classNames: ['text-pagination'],
  tagName: 'hbox',
  @alias('textPageNumber')
  currentPage: null
});

export default connect(stateToComputed, dispatchToActions)(reconTextPagination);

import Component from 'ember-component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { pageFirst, pagePrevious, pageNext, pageLast } from 'recon/actions/data-creators';
import { packetTotal } from 'recon/reducers/header/selectors';

const stateToComputed = ({ recon, recon: { packets } }) => ({
  pageNumber: packets.pageNumber,
  packetsTotal: packetTotal(recon),
  packetsPerPage: packets.packetsPageSize
});

const dispatchToActions = {
  pageFirst,
  pagePrevious,
  pageNext,
  pageLast
};

const reconDataPagination = Component.extend({
  layout,

  classNames: ['data-pagination'],

  @computed('pageNumber', 'packetsPerPage', 'packetsTotal')
  pageInformation(pageState, packetsPerPage, packetsTotal) {
    const lastPageNumber = (Math.floor(packetsTotal / packetsPerPage) === 0 ? 1 : Math.floor(packetsTotal / packetsPerPage));
    return `${this.get('pageNumber')} of ${lastPageNumber}`;
  },

  @computed('pageNumber')
  isPreviousPageDisabled(pageNumber) {
    return pageNumber === 1;
  },

  @computed('pageNumber', 'packetsPerPage', 'packetsTotal')
  isNextPageDisabled(pageNumber, packetsPerPage, packetsTotal) {
    return (Math.floor(packetsTotal / packetsPerPage) <= pageNumber);
  }

});

export default connect(stateToComputed, dispatchToActions)(reconDataPagination);

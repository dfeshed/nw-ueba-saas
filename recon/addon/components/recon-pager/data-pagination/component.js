import Component from 'ember-component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { pageFirst, pagePrevious, pageNext, pageLast, jumpToPage, changePacketsPerPage } from 'recon/actions/data-creators';
import { packetTotal } from 'recon/reducers/header/selectors';
import { lastPageNumber, cannotGoToNextPage, cannotGoToPreviousPage } from 'recon/reducers/packets/selectors';
import service from 'ember-service/inject';

const stateToComputed = ({ recon, recon: { packets } }) => ({
  pageNumber: packets.pageNumber,
  packetsTotal: packetTotal(recon),
  lastPageNumber: lastPageNumber(recon),
  cannotGoToNextPage: cannotGoToNextPage(recon),
  cannotGoToPreviousPage: cannotGoToPreviousPage(recon),
  packetsPageSize: packets.packetsPageSize
});

const dispatchToActions = {
  pageFirst,
  pagePrevious,
  pageNext,
  pageLast,
  jumpToPage,
  changePacketsPerPage
};

const PACKETS_PER_PAGE = [
  100,
  300,
  500
];

const reconDataPagination = Component.extend({
  layout,
  classNames: ['data-pagination'],
  options: PACKETS_PER_PAGE,
  i18n: service(),

  @computed('pageNumber')
  currentPage(pageNumber) {
    return `${pageNumber}`;
  },

  @computed('i18n')
  packetsPerPageText(i18n) {
    return i18n.t('recon.reconPager.packetsPerPageText');
  },

  actions: {
    setPageNumber(event) {
      if (event.keyCode === 13) {
        const newPage = event.target.value;
        this.send('jumpToPage', newPage);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(reconDataPagination);

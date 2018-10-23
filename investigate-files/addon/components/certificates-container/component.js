import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';

import {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters,
  getFilter
} from 'investigate-shared/actions/data-creators/filter-creators';
import { getFirstPageOfCertificates, getPageOfCertificates, toggleCertificateView } from 'investigate-files/actions/certificate-data-creators';
import { FILTER_TYPES } from './filter-types';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';

const stateToComputed = (state) => ({
  filter: state.certificate.filter,
  savedFilter: savedFilter(state.certificate),
  selectedFilterId: selectedFilterId(state.certificate),
  certificateFilters: state.certificate.filter.savedFilterList,
  isEndpointServerOnline: !state.endpointServer.isSummaryRetrieveError

});

const dispatchToActions = {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters,
  getFirstPageOfCertificates,
  getPageOfCertificates,
  getFilter,
  toggleCertificateView
};

const Certificate = Component.extend({

  tagName: 'vbox',

  classNames: ['certificates-container', 'main-zone'],

  filterTypes: FILTER_TYPES,

  init() {
    this._super(...arguments);
    next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.send('getFilter', () => {}, 'CERTIFICATE');
      }
    });
  }

});

export default connect(stateToComputed, dispatchToActions)(Certificate);


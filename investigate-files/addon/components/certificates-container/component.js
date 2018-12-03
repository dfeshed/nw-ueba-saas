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
import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { serviceList } from 'investigate-files/reducers/file-list/selectors';
import { getAllServices } from 'investigate-files/actions/data-creators';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  filter: state.certificate.filter,
  savedFilter: savedFilter(state.certificate),
  selectedFilterId: selectedFilterId(state.certificate),
  certificateFilters: state.certificate.filter.savedFilterList,
  selectedServiceData: selectedServiceWithStatus(state),
  selections: state.certificate.list.selectedCertificateList,
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  serviceList: serviceList(state)
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
  toggleCertificateView,
  getAllServices
};

const Certificate = Component.extend({

  tagName: 'vbox',

  classNames: ['certificates-container', 'main-zone'],

  filterTypes: FILTER_TYPES,

  @computed('selections')
  pivotInvestigateDisabled(selections) {
    if (!selections) {
      return true;
    }
    return !(selections.length === 1);
  },

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


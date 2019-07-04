import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';

import {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters,
  getFilter
} from 'investigate-shared/actions/data-creators/filter-creators';

import { getFirstPageOfCertificates, getPageOfCertificates } from 'investigate-files/actions/certificate-data-creators';
import { FILTER_TYPES } from './filter-types';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { serviceList } from 'investigate-files/reducers/file-list/selectors';
import { getAllServices } from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  filter: state.certificate.filter,
  savedFilter: savedFilter(state.certificate),
  selectedFilterId: selectedFilterId(state.certificate),
  certificateFilters: state.certificate.filter.savedFilterList,
  selectedServiceData: selectedServiceWithStatus(state),
  selections: state.certificate.list.selectedCertificateList,
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  serviceList: serviceList(state),
  isCertificateView: state.certificate.list.isCertificateView
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
  getAllServices
};

const Certificate = Component.extend({

  tagName: 'vbox',

  classNames: ['rsa-investigate-files', 'certificates-container', 'main-zone'],

  filterTypes: FILTER_TYPES,

  pivot: service(),

  contextualHelp: service(),

  init() {
    this._super(...arguments);
    next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.send('getFilter', () => {}, 'CERTIFICATE');
      }
    });
  },

  actions: {
    pivotToInvestigate(item, category) {
      this.get('pivot').pivotToInvestigate('thumbprint', item, category);
    },

    gotoFilesView(isCertificateView) {
      const contextualTopic = isCertificateView ? this.get('contextualHelp.invFiles') : this.get('contextualHelp.invEndpointCertificates');
      this.set('contextualHelp.topic', contextualTopic);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(Certificate);


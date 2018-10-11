import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';

import { setEndpointServer } from 'configure/actions/creators/endpoint/server-creator';
import {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters,
  getFilter
} from 'investigate-shared/actions/data-creators/filter-creators';
import { getFirstPageOfCertificates } from 'configure/actions/creators/endpoint/certificates-creator';
import { FILTER_TYPES } from './filter-types';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';

const stateToComputed = (state) => ({
  servers: state.configure.endpoint.server,
  serverId: state.configure.endpoint.query.serverId,
  isEndpointServerOnline: !state.configure.endpoint.server.isSummaryRetrieveError,
  filter: state.configure.endpoint.certificates.filter,
  savedFilter: savedFilter(state.configure.endpoint.certificates),
  selectedFilterId: selectedFilterId(state.configure.endpoint.certificates),
  certificateFilters: state.configure.endpoint.certificates.filter.savedFilterList

});

const dispatchToActions = {
  setEndpointServer,
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters,
  getFirstPageOfCertificates,
  getFilter
};

const Certificate = Component.extend({

  tagName: 'vbox',

  classNames: ['certificates-container', 'main-zone'],

  filterTypes: FILTER_TYPES,

  init() {
    this._super(...arguments);
    next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.send('getFilter', getFirstPageOfCertificates, 'CERTIFICATE');
      }
    });
  }

});

export default connect(stateToComputed, dispatchToActions)(Certificate);


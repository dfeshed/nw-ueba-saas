import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, tagName } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
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

@classic
@tagName('vbox')
@classNames('rsa-investigate-files', 'certificates-container', 'main-zone')
class Certificate extends Component {
  filterTypes = FILTER_TYPES;

  @service
  pivot;

  @service
  contextualHelp;

  init() {
    super.init(...arguments);
    next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.send('getFilter', () => {}, 'CERTIFICATE');
      }
    });
  }

  @action
  pivotToInvestigate(item, category) {
    this.get('pivot').pivotToInvestigate('thumbprint', item, category);
  }

  @action
  gotoFilesView(isCertificateView) {
    const contextualTopic = isCertificateView ? this.get('contextualHelp.invFiles') : this.get('contextualHelp.invEndpointCertificates');
    this.set('contextualHelp.topic', contextualTopic);
  }
}

export default connect(stateToComputed, dispatchToActions)(Certificate);


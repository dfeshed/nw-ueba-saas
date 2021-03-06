import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, tagName } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { certificatesLoading, columns, nextLoadCount } from 'investigate-files/reducers/certificates/selectors';
import { connect } from 'ember-redux';
import {
  getPageOfCertificates,
  toggleCertificateSelection,
  saveCertificateStatus,
  getSavedCertificateStatus,
  sortBy
} from 'investigate-files/actions/certificate-data-creators';
import { saveColumnConfig } from 'investigate-files/actions/data-creators';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';

const stateToComputed = (state) => ({
  certificatesItems: state.certificate.list.certificatesList,
  loadMoreStatus: state.certificate.list.loadMoreStatus,
  selections: state.certificate.list.selectedCertificateList || [],
  areCertificatesLoading: certificatesLoading(state),
  isCertificateView: state.certificate.list.isCertificateView,
  certificatesColumns: columns(state),
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  nextLoadCount: nextLoadCount(state),
  sortField: state.certificate.list.sortField,
  isSortDescending: state.certificate.list.isSortDescending
});

const dispatchToActions = {
  getPageOfCertificates,
  toggleCertificateSelection,
  saveCertificateStatus,
  getSavedCertificateStatus,
  saveColumnConfig,
  sortBy
};

@classic
@tagName('')
@classNames('certificates-data-table')
class Certificates extends Component {
  isAllSelected = true;
  showModal = false;

  @service
  timezone;

  isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('thumbprint', item.thumbprint);
    }
    return selected;
  }

  @action
  toggleSelectedRow(item, index, e, table) {
    table.set('selectedIndex', index);
    this.send('toggleCertificateSelection', item);
    e.preventDefault();
  }

  @action
  showStatusWindow() {
    const selections = this.get('selections');
    if (selections && selections.length === 1) {
      this.send('getSavedCertificateStatus', selections.mapBy('thumbprint'));
    }
    this.set('showModal', true);
  }

  @action
  closeModal() {
    this.set('showModal', false);
  }

  @action
  beforeContextMenuShow({ contextSelection: item }) {

    if (!this.isAlreadySelected(this.get('selections'), item)) {
      this.send('toggleCertificateSelection', item);
    }
    const selections = this.get('selections');
    if (selections && selections.length === 1) {
      this.send('getSavedCertificateStatus', selections);
    }
  }

  /**
   * Abort the action if dragged column is file name, risk score and checkbox also abort if column in dropped to
   * file name, risk score and checkbox.
   *
   */
  @action
  onReorderColumns(columns, newColumns, column, fromIndex, toIndex) {
    return !(column.dataType === 'radio' ||
      column.field === 'friendlyName' ||
      column.field === 'certificateStatus' ||
      toIndex === 0 ||
      toIndex === 1 ||
      toIndex === 2);
  }

  @action
  onColumnConfigChange(changedProperty, changedColumns) {
    this.send('saveColumnConfig', changedProperty, changedColumns, 'files-certificates');
  }

  @action
  sort(column) {
    const isDesc = this.get('isSortDescending');
    const { field: sortField } = column;
    this.send('sortBy', sortField, !isDesc);
    column.set('isDescending', !isDesc);
  }
}

export default connect(stateToComputed, dispatchToActions)(Certificates);

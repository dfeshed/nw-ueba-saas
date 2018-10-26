import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';
import {
  fileCountForDisplay,
  serviceList,
  isAllSelected,
  files,
  checksums
} from 'investigate-files/reducers/file-list/selectors';
import { columns } from 'investigate-files/reducers/schema/selectors';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import {
  sortBy,
  getPageOfFiles,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  getAllServices,
  saveFileStatus,
  getSavedFileStatus,
  fetchHostNameList,
  retrieveRemediationStatus,
  setSelectedFile
} from 'investigate-files/actions/data-creators';

import { resetRiskScore, getRiskScoreContext, resetRiskContext } from 'investigate-shared/actions/data-creators/risk-creators';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { navigateToInvestigateEventsAnalysis } from 'investigate-shared/utils/pivot-util';
import { success, failure } from 'investigate-shared/utils/flash-messages';

const stateToComputed = (state) => ({
  serviceList: serviceList(state),
  columnConfig: columns(state),
  loadMoreStatus: state.files.fileList.loadMoreStatus,
  areFilesLoading: state.files.fileList.areFilesLoading,
  files: files(state), // All visible files
  totalItems: fileCountForDisplay(state),
  sortField: state.files.fileList.sortField, // Currently applied sort on file list
  isSortDescending: state.files.fileList.isSortDescending,
  isAllSelected: isAllSelected(state),
  selections: state.files.fileList.selectedFileList,
  checksums: checksums(state),
  agentCountMapping: state.files.fileList.agentCountMapping,
  fileStatusData: state.files.fileList.fileStatusData,
  remediationStatus: state.files.fileList.isRemediationAllowed,
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  isCertificateView: state.certificate.list.isCertificateView
});

const dispatchToActions = {
  sortBy,
  getPageOfFiles,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  getAllServices,
  saveFileStatus,
  getSavedFileStatus,
  fetchHostNameList,
  getRiskScoreContext,
  retrieveRemediationStatus,
  setSelectedFile,
  resetRiskScore,
  resetRiskContext
};

/**
 * File list component for displaying the list of files
 * @public
 */
const FileList = Component.extend({

  tagName: '',

  accessControl: service(),

  timezone: service(),

  FIXED_COLUMNS: [
    {
      dataType: 'checkbox',
      width: 20,
      class: 'rsa-form-row-checkbox',
      componentClass: 'rsa-form-checkbox',
      visible: true,
      disableSort: true,
      headerComponentClass: 'rsa-form-checkbox'
    },
    {
      dataType: 'string',
      width: 200,
      visible: true,
      field: 'firstFileName',
      searchable: true,
      title: 'investigateFiles.fields.firstFileName'
    },
    {
      dataType: 'string',
      width: 100,
      visible: true,
      field: 'score',
      searchable: false,
      title: 'investigateFiles.fields.score'
    },
    {
      dataType: 'string',
      width: 100,
      visible: true,
      field: 'machineCount',
      searchable: false,
      disableSort: true,
      title: 'investigateFiles.fields.machineCount'
    }
  ],

  CONFIG_FIXED_COLUMNS: ['firstFileName', 'score'],

  showServiceModal: false,

  showFileStatusModal: false,

  showResetScoreModal: false,

  itemList: [],

  selectedFiles: null,

  @computed('isCertificateView')
  showColumnChooser(isCertificateView) {
    return !isCertificateView;
  },

  @computed('columnConfig')
  updatedColumns(columns) {
    const UPDATED_COLUMNS = columns.filter((column) => !this.CONFIG_FIXED_COLUMNS.includes(column.field));
    return this._sortList(UPDATED_COLUMNS);
  },

  @computed('fileStatusData')
  data(fileStatusData) {
    return { ...fileStatusData };
  },

  _sortList(columnList) {
    const i18n = this.get('i18n');
    const sortList = _.sortBy(columnList, [(column) => {
      return i18n.t(column.title).toString();
    }]);
    return this.FIXED_COLUMNS.concat(sortList);
  },

  init() {
    this._super(arguments);
    next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.send('getAllServices');
      }
    });
  },

  isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('checksumSha256', item.checksumSha256);
    }
    return selected;
  },


  actions: {
    toggleSelectedRow(item, index, e, table) {
      const { target: { classList } } = e;
      if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
        const isSameRowClicked = table.get('selectedIndex') === index;
        const openRiskPanel = this.get('openRiskPanel');
        table.set('selectedIndex', index);
        if (!isSameRowClicked && openRiskPanel) {
          this.send('resetRiskContext');
          this.send('setSelectedFile', item);
          this.send('fetchHostNameList', item.checksumSha256);
          this.send('getRiskScoreContext', item.checksumSha256, 'critical');
          next(() => {
            this.openRiskPanel();
          });
        } else {
          this.closeRiskPanel();
          table.set('selectedIndex', null);
        }
      }
    },

    showRiskScoreModal(fileList) {
      this.set('selectedFiles', fileList);
      this.set('showResetScoreModal', true);
    },

    resetRiskScoreAction() {
      const callBackOptions = {
        onSuccess: () => {
          success('investigateFiles.riskScore.success');
        },
        onFailure: () => failure('investigateFiles.riskScore.error')
      };
      this.set('showResetScoreModal', false);
      this.send('resetRiskScore', this.get('selectedFiles'), callBackOptions);
      this.set('selectedFiles', null);
    },

    toggleItemSelection(item) {
      this.send('toggleFileSelection', item);
    },

    toggleAllSelection() {
      if (!this.get('isAllSelected')) {
        this.send('selectAllFiles');
      } else {
        this.send('deSelectAllFiles');
      }
    },

    showEditFileStatus(item) {
      if (this.get('accessControl.endpointCanManageFiles')) {
        this.set('itemList', [item]);
        this.set('showFileStatusModal', true);
      } else {
        failure('investigateFiles.noManagePermissions');
      }
    },

    showServiceList(item) {
      this.set('itemList', [item]);
      const serviceId = this.get('serviceId');
      if (serviceId !== '-1') {
        const { itemList, timeRange } = this.getProperties('itemList', 'timeRange');
        const { zoneId } = this.get('timezone.selected');
        navigateToInvestigateEventsAnalysis({ metaName: 'checksumSha256', itemList }, serviceId, timeRange, zoneId);
      } else {
        this.set('showServiceModal', true);
      }
    },

    onCloseServiceModal() {
      this.set('showServiceModal', false);
    },

    onCloseEditFileStatus() {
      this.set('showFileStatusModal', false);
    },

    onResetScoreModalClose() {
      this.set('showResetScoreModal', false);
    },

    beforeContextMenuShow(item) {
      this.closeRiskPanel();
      this.set('itemList', [item]);
      if (!this.isAlreadySelected(this.get('selections'), item)) {
        this.send('deSelectAllFiles');
        this.send('toggleFileSelection', item);
      }
      const selections = this.get('selections');
      if (selections && selections.length === 1) {
        this.send('getSavedFileStatus', selections);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FileList);

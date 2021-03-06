import Component from '@ember/component';
import layout from './template';
import { externalLookup } from 'investigate-shared/utils/file-external-lookup';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

const FileActionConf = [
  {
    panelId: 'panel1',
    name: 'googleLookup',
    subItems: [
      { name: 'fileName', type: 'google' },
      { name: 'md5', type: 'google' },
      { name: 'sha1', type: 'google' },
      { name: 'sha256', type: 'google' }
    ]
  },
  {
    panelId: 'panel2',
    name: 'virusTotalLookup',
    subItems: [
      { name: 'md5', type: 'VirusTotal' },
      { name: 'sha1', type: 'VirusTotal' },
      { name: 'sha256', type: 'VirusTotal' }
    ]
  }
];

export default Component.extend({
  layout,

  tagName: 'hbox',

  classNames: ['file-actionbar flexi-fit'],

  i18n: service(),

  isDisplayTabLabel: false,

  accessControl: service(),

  pivot: service(),

  showOnlyIcons: false,

  serviceList: null,

  itemList: null,

  metaName: null,

  getAllServices: null,

  selectedFileCount: null,

  showResetRiskScore: false,

  showDownloadProcessDump: false,

  isFileTab: false,

  listAllFiles: true,

  isSnapshotsAvailable: false,

  @computed('itemList')
  isEditStatusButtonDisabled(itemList) {
    if (!itemList) {
      return true;
    }
    return !(itemList.length > 0);
  },

  @computed('itemList', 'isAgentMigrated')
  isProcessDumpDownloadDisabled(itemList, isAgentMigrated) {
    return (itemList && itemList.length != 1) || isAgentMigrated;
  },

  @computed(
    'fileDownloadButtonStatus',
    'showResetRiskScore', 'itemList',
    'showDownloadProcessDump',
    'isProcessDumpDownloadDisabled'
  )
  fileActionOptions(
    fileDownloadButtonStatus,
    showResetRiskScore, itemList,
    showDownloadProcessDump,
    isProcessDumpDownloadDisabled
  ) {
    const i18n = this.get('i18n');
    const canManageFiles = this.get('accessControl.endpointCanManageFiles');
    let fileActionConfClone = [...FileActionConf];

    if (fileDownloadButtonStatus && canManageFiles) {
      const { isDownloadToServerDisabled, isSaveLocalAndFileAnalysisDisabled } = fileDownloadButtonStatus;
      // Additional menu options enabled and disabled based on selected file's download status.
      // Adding download process dump option
      if (showDownloadProcessDump) {
        fileActionConfClone.push({ panelId: 'panel7', name: 'downloadProcessDumpToServer', disabled: isProcessDumpDownloadDisabled });
      }
      fileActionConfClone = [
        ...fileActionConfClone,
        { panelId: 'panel3', name: 'downloadToServer', disabled: isDownloadToServerDisabled },
        { panelId: 'panel4', name: 'saveLocalCopy', disabled: isSaveLocalAndFileAnalysisDisabled },
        { panelId: 'panel5', name: 'analyzeFile', disabled: isSaveLocalAndFileAnalysisDisabled }
      ];
    }
    // Reset riskscore option Adding last
    if (showResetRiskScore) {
      fileActionConfClone.push({ panelId: 'panel6', name: 'resetRiskScore' });
    }

    // Translated titles added.
    return fileActionConfClone.map((item) => {
      const title = i18n.t(`investigateShared.endpoint.fileActions.${item.name}`);
      let disabledTooltip = i18n.t(`investigateShared.endpoint.fileActions.tooltips.${item.name}`);
      if ((item.name === 'downloadToServer') && (itemList.length !== 1)) {
        // Setting download disabled tooltip
        disabledTooltip = this.get('downloadDisabledTooltip');
      }
      let { subItems } = item;
      if (subItems) {
        subItems = subItems.map((subItem) => {
          const title = i18n.t(`investigateShared.endpoint.fileActions.${subItem.name}`);
          return { ...subItem, title };
        });
        return { ...item, title, subItems };
      }
      return { ...item, title, disabledTooltip };
    });
  },

  @computed('itemList')
  isMaxResetRiskScoreLimit(itemList) {
    return itemList.length > 100;
  },

  @computed('isSnapshotsAvailable')
  disableToggle() {
    return !this.isSnapshotsAvailable;
  },

  @computed('isSnapshotsAvailable')
  allFilesTooltip() {
    const i18n = this.get('i18n');
    return this.isSnapshotsAvailable ? i18n.t('investigateHosts.files.toolTip.allFilesToggle') : i18n.t('investigateHosts.files.toolTip.noSnapShotToggle');
  },

  actions: {
    onFileAction(action) {
      const selectedList = this.get('itemList');
      externalLookup(action, selectedList);
    },
    onFileDownloadOptions(selectedButton, isDisabled) {
      if (!isDisabled) {
        switch (selectedButton) {
          case 'downloadToServer' :
            this.get('downloadFiles')();
            break;
          case 'saveLocalCopy' :
            this.get('saveLocalCopy')();
            break;
          case 'analyzeFile' :
            this.get('analyzeFile')();
            break;
          case 'downloadProcessDumpToServer' :
            this.get('downloadProcessDump')();
            break;
        }
      }
    },

    onDownloadProcessDump() {
      this.get('downloadProcessDump')();
    },

    onResetAction() {
      this.set('showResetScoreModal', true);
    },

    onResetScoreModalClose() {
      this.set('showResetScoreModal', false);
    },

    resetRiskScoreAction() {
      const selectedList = this.get('itemList');
      this.resetRiskScore(selectedList.slice(0, 100));
      this.set('showResetScoreModal', false);
    },

    pivotToInvestigate(item, category) {
      const machineName = this.get('hostName');
      this.get('pivot').pivotToInvestigate(this.get('metaName'), { ...item, machineName }, category);
    },

    toggleAllFile() {
      this.get('filesToggle')();
    }

  }

});

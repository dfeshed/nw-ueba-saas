import Component from '@ember/component';
import layout from './template';
import { externalLookup } from 'investigate-shared/utils/file-external-lookup';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

const FileActionConf = [
  { panelId: 'panel1', name: 'googleLookup',
    subItems: [
    { name: 'fileName', type: 'google' },
    { name: 'md5', type: 'google' },
    { name: 'sha1', type: 'google' },
    { name: 'sha256', type: 'google' }
    ] },
  { panelId: 'panel2', name: 'virusTotalLookup',
    subItems: [
    { name: 'md5', type: 'VirusTotal' },
    { name: 'sha1', type: 'VirusTotal' },
    { name: 'sha256', type: 'VirusTotal' }
    ] }
];

export default Component.extend({
  layout,

  tagName: 'hbox',

  classNames: ['file-actionbar'],

  i18n: service(),

  showOnlyIcons: false,

  serviceList: null,

  itemList: null,

  metaName: null,

  getAllServices: null,

  selectedFileCount: null,

  @computed('itemList')
  isEditStatusButtonDisabled(itemList) {
    if (!itemList) {
      return true;
    }
    return !(itemList.length > 0);
  },

  @computed('itemList')
  pivotInvestigateDisabled(itemList) {
    if (!itemList) {
      return true;
    }
    return !(itemList.length === 1);
  },

  @computed('itemList')
  statusData(selectedFileList) {
    if (selectedFileList && selectedFileList.length === 1) {
      return selectedFileList[0].fileStatusData || {};
    }
    return {};
  },

  @computed('fileDownloadButtonStatus')
  fileActionOptions({ isDownloadToServerDisabled, isSaveLocalAndFileAnalysisDisabled }) {
    const i18n = this.get('i18n');
    let fileActionConfClone = [...FileActionConf];

    // Additional menu options enabled and disabled based on selected file's download status.
    fileActionConfClone = [
      ...fileActionConfClone,
      { panelId: 'panel3', name: 'downloadToServer', disabled: isDownloadToServerDisabled },
      { panelId: 'panel4', name: 'saveLocalCopy', disabled: isSaveLocalAndFileAnalysisDisabled },
      { panelId: 'panel5', name: 'analyzeFile', disabled: isSaveLocalAndFileAnalysisDisabled }
    ];

    // Translated titles added.
    return fileActionConfClone.map((item) => {
      const title = i18n.t(`investigateShared.endpoint.fileActions.${item.name}`).string;
      let { subItems } = item;
      if (subItems) {
        subItems = subItems.map((subItem) => {
          const title = i18n.t(`investigateShared.endpoint.fileActions.${subItem.name}`).string;
          return { ...subItem, title };
        });
        return { ...item, title, subItems };
      }
      return { ...item, title };
    });
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
        }
      }
    }
  }

});

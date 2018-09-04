import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
const NOT_SUPPORTED_OS = ['linux', 'mac'];

export default Component.extend({
  layout,

  tagName: 'hbox',

  classNames: ['file-actionbar'],

  showOnlyIcons: false,

  serviceList: null,

  itemList: null,

  metaName: null,

  getAllServices: null,

  selectedFileCount: null,

  @computed('itemList')
  isEditStatusButtonDisabled(selectedFilesList) {
    if (selectedFilesList.length > 0) {
      return selectedFilesList.some((item) => NOT_SUPPORTED_OS.includes(item.machineOSType));
    }
    return true;
  },

  @computed('selectedFileCount')
  pivotInvestigateDisabled(selectedFilesList) {
    return !(selectedFilesList === 1);
  },

  @computed('itemList')
  statusData(selectedFileList) {
    if (selectedFileList && selectedFileList.length === 1) {
      return selectedFileList[0].fileStatusData || {};
    }
    return {};
  }

});

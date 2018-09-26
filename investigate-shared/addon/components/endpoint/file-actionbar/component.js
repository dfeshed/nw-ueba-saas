import Component from '@ember/component';
import layout from './template';
import { externalLookup } from 'investigate-shared/utils/file-external-lookup';
import computed from 'ember-computed-decorators';

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

  fileActionConf: [
    { panelId: 'panel1', name: 'Download to server', title: 'Download to server' },
    { panelId: 'panel2', name: 'Google Lookup', title: 'Google Lookup',
      subItems: [
      { title: 'File name', name: 'fileName', type: 'google' },
      { title: 'MD5', name: 'md5', type: 'google' },
      { title: 'SHA1', name: 'sha1', type: 'google' },
      { title: 'SHA256', name: 'sha256', type: 'google' }
      ] },
    { panelId: 'panel3', name: 'VirusTotal Lookup', title: 'VirusTotal Lookup',
      subItems: [
      { title: 'MD5', name: 'md5', type: 'VirusTotal' },
      { title: 'SHA1', name: 'sha1', type: 'VirusTotal' },
      { title: 'SHA256', name: 'sha256', type: 'VirusTotal' }
      ] }
  ],

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
  actions: {
    onFileAction(action) {
      const selectedList = this.get('itemList');
      externalLookup(action, selectedList);
    }
  }

});

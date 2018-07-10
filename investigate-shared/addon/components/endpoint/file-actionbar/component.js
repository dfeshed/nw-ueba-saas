import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  tagName: 'hbox',

  classNames: ['file-actionbar'],

  showOnlyIcons: false,

  serviceList: null,

  item: null,

  metaName: null,

  getAllServices: null,

  selectedFileCount: null,

  showFileStatusModal: false,

  @computed('selectedFileCount')
  hasNoSelection(selectedFilesList) {
    return !(selectedFilesList > 0);
  },

  @computed('selectedFileCount')
  pivotInvestigateDisabled(selectedFilesList) {
    return !(selectedFilesList === 1);
  },

  actions: {
    showEditFileStatusModal() {
      this.set('showFileStatusModal', true);
    },

    onClose() {
      this.set('showFileStatusModal', false);
    }
  }
});

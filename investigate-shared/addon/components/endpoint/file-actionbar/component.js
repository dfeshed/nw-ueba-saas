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

  @computed('selectedFileCount')
  hasNoSelection(selectedFilesList) {
    return !(selectedFilesList > 0);
  },

  @computed('selectedFileCount')
  pivotInvestigateDisabled(selectedFilesList) {
    return !(selectedFilesList === 1);
  }

});

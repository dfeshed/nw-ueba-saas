import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  classNames: ['file-actionbar'],
  showOnlyIcons: false,
  serviceList: null,
  item: null,
  metaName: null,
  getAllServices: null,
  selectedFileCount: null,

  @computed('selectedFileCount')
  hasNoSelection(selectedFileCount) {
    return !(selectedFileCount > 0);
  },

  @computed('selectedFileCount')
  pivotInvestigateDisabled(selectedFileCount) {
    return !(selectedFileCount === 1);
  }
});

import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { failure } from 'investigate-shared/utils/flash-messages';

export default Component.extend({
  classNames: ['simple-detail-display-wrapper'],

  tagName: 'hbox',

  selections: null,

  showFileStatusModel: false,

  accessControl: service(),

  rowItem: null,

  isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('checksumSha256', item.checksumSha256);
    }
    return selected;
  },

  /*
   * A hash of inputs coming from consumers of this component.
   *
   * Possible values:
   *   status
   *   tableItems
   *   columnsConfig
   *   selectRowAction
   *   localeNameSpace
   *   propertyConfig
   *   propertyData
   */
  detailDisplayInputs: null,

  /* Sets the class as col-xs-9 only if the property panal is present for the selected host details tab */
  @computed('detailDisplayInputs')
  datatableWidth(detailDisplayInputs) {
    return (detailDisplayInputs && detailDisplayInputs.propertyConfig) ? 'col-xs-9' : 'col-xs-12';
  },

  actions: {
    beforeContextMenuShow(item) {
      if (!this.isAlreadySelected(this.get('detailDisplayInputs.selectedFiles'), item)) {
        this.detailDisplayInputs.toggleOneItemSelectionAction(item);
      }
      const selections = this.get('selections');
      if (selections && selections.length === 1) {
        this.detailDisplayInputs.getSavedFileStatus();
      }
    },
    showEditFileStatus(item) {
      if (this.get('accessControl.endpointCanManageFiles')) {
        this.set('rowItem', item);
        this.set('showFileStatusModal', true);
      } else {
        failure('investigateFiles.noManagePermissions');
      }
    },
    onCloseEditFileStatus() {
      this.set('showFileStatusModal', false);
    },
    resetRiskScoreAction() {
      // Placeholder for the next PR.
    }
  }
});
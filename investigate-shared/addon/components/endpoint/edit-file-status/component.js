import Component from '@ember/component';
import layout from './template';
import { inject as service } from '@ember/service';
import { failure } from 'investigate-shared/utils/flash-messages';

/**
 * Edit file status component to change the status of the file(s)
 * @public
 */
export default Component.extend({
  layout,

  accessControl: service(),

  classNames: 'edit-file-status',

  showOnlyIcons: false,

  isDisabled: false,

  showFileStatusModal: false,

  actions: {
    showEditFileStatusModal() {
      if (this.get('accessControl.endpointCanManageFiles')) {
        const selections = this.get('itemList');
        this.retrieveRemediationStatus(selections);
        this.set('showFileStatusModal', true);
        if (selections && selections.length === 1) {
          this.getSavedFileStatus(selections);
        }
      } else {
        failure('investigateFiles.noManagePermissions');
      }
    },
    closeModal() {
      this.set('showFileStatusModal', false);
    }
  }
});

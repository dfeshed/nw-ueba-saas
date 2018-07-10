import Component from '@ember/component';
import layout from './template';

import { success } from 'investigate-shared/utils/flash-messages';

/**
 * Edit file status component to change the status of the file(s)
 * @public
 */
export default Component.extend({
  layout,

  classNames: 'edit-file-status',

  showFileStatusModal: false,

  isIconOnly: false,

  isDisabled: false,

  data: {
    fileStatus: 'Neutral',
    fileCategory: 'Apt',
    comment: ''
  },

  radioButtons: [
    {
      label: 'investigateFiles.editFileStatus.fileStatusOptions.neutral',
      value: 'Neutral'
    },
    {
      label: 'investigateFiles.editFileStatus.fileStatusOptions.whitelist',
      value: 'Whitelist'
    },
    {
      label: 'investigateFiles.editFileStatus.fileStatusOptions.graylist',
      value: 'Graylist'
    },
    {
      label: 'investigateFiles.editFileStatus.fileStatusOptions.knowngood',
      value: 'KnownGood'
    },
    {
      label: 'investigateFiles.editFileStatus.fileStatusOptions.blacklist',
      value: 'Blacklist'
    }
  ],

  fileCategories: [
    'Apt',
    'AttackerTool',
    'GenericMalware',
    'Ransom',
    'Unidentified'
  ],


  actions: {

    showEditFileStatusModal() {
      this.set('showFileStatusModal', true);
    },

    saveFileStatus() {
      const callback = {
        onSuccess: () => {
          success('investigateFiles.editFileStatus.successMessage');
          this.set('showFileStatusModal', false);
        }
      };
      this.onSaveFileStatus(this.get('data'), callback);
    },

    closeModal() {
      this.set('showFileStatusModal', false);
    },

    setFileCategory(category) {
      this.set('data.fileCategory', category);
    }

  }
});

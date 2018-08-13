import Component from '@ember/component';
import layout from './template';
import { success } from 'investigate-shared/utils/flash-messages';

export default Component.extend({
  layout,

  data: {
    fileStatus: 'Neutral',
    fileCategory: 'Apt',
    comment: '',
    remediationAction: ''
  },

  radioButtons: [
    {
      label: 'investigateFiles.editFileStatus.fileStatusOptions.blacklist',
      value: 'Blacklist'
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
      label: 'investigateFiles.editFileStatus.fileStatusOptions.whitelist',
      value: 'Whitelist'
    },
    {
      label: 'investigateFiles.editFileStatus.fileStatusOptions.neutral',
      value: 'Neutral'
    }
  ],

  _closeModal() {
    this.set('data', {
      fileStatus: 'Neutral',
      fileCategory: 'Apt',
      comment: '',
      remediationAction: ''
    });
    const closeModal = this.get('closeModal');
    if (closeModal) {
      closeModal();
    }
  },

  actions: {
    saveFileStatus() {
      const callback = {
        onSuccess: () => {
          this._closeModal();
          success('investigateFiles.editFileStatus.successMessage');
        }
      };
      this.onSaveFileStatus(this.get('data'), callback);
    },

    onCancel() {
      this._closeModal();
    }
  }
});

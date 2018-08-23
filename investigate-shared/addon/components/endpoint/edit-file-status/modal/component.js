import Component from '@ember/component';
import layout from './template';
import { success } from 'investigate-shared/utils/flash-messages';
import { isEmpty } from '@ember/utils';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  data: {
    fileStatus: null,
    category: null,
    comment: '',
    remediationAction: null
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
    const closeModal = this.get('closeModal');
    if (closeModal) {
      closeModal();
      this.set('data', {});
    }
  },

  @computed('data.comment')
  isSaveButtonDisabled(comment) {
    return isEmpty(comment);
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

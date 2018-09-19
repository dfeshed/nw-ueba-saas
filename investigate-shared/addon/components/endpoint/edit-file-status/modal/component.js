import Component from '@ember/component';
import layout from './template';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { isEmpty } from '@ember/utils';
import computed from 'ember-computed-decorators';
import { lookup } from 'ember-dependency-lookup';

const STATUS_WITH_REMEDIATION = ['Blacklist', 'Graylist'];

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
    }
  },

  @computed('data.comment', 'data.fileStatus')
  isSaveButtonDisabled(comment, fileStatus) {
    return isEmpty(comment) || isEmpty(fileStatus);
  },

  actions: {
    saveFileStatus() {
      const request = lookup('service:request');
      return request.ping('contexthub-server-ping')
        .then(() => {
          const callback = {
            onSuccess: () => {
              this._closeModal();
              success('investigateFiles.editFileStatus.successMessage');
            },
            onFailure: () => {
              this.set('isSaveButtonDisabled', true);
              failure('investigateFiles.editFileStatus.contexthubServerOffline');
            }
          };
          if (!STATUS_WITH_REMEDIATION.includes(this.get('data').fileStatus)) {
            this.set('data.remediationAction', null);
            this.set('data.category', null);
          }
          this.onSaveFileStatus(this.get('data'), callback);
        })
        .catch(() => {
          this.set('isSaveButtonDisabled', true);
          failure('investigateFiles.editFileStatus.contexthubServerOffline');
        });
    },
    onCancel() {
      this._closeModal();
    }
  }
});

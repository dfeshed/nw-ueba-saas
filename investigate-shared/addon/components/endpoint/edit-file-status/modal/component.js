import Component from '@ember/component';
import layout from './template';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { isEmpty } from '@ember/utils';
import computed from 'ember-computed-decorators';
import { lookup } from 'ember-dependency-lookup';
import { hasRestrictedEntry, isAllAreRestrictedEntry } from 'investigate-shared/utils/file-status-util';
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
    this.set('formData', {});
    if (closeModal) {
      closeModal();
    }
  },

  @computed('data')
  formData(data) {
    return { ...data }; // required this to reset the data on cancel
  },

  @computed('formData.comment', 'formData.fileStatus')
  isSaveButtonDisabled(comment, fileStatus) {
    return isEmpty(comment) || isEmpty(fileStatus);
  },

  @computed('formData.comment')
  isCharacterLimitReached(comment) {
    return comment && comment.length >= 900;
  },

  @computed('itemList', 'restrictedFileList')
  showWhiteListWarning(itemList, restrictedFileList) {
    return hasRestrictedEntry(itemList.mapBy('fileName'), restrictedFileList);
  },

  @computed('itemList', 'restrictedFileList')
  disableRadio(itemList, restrictedFileList) {
    return itemList && isAllAreRestrictedEntry(itemList.mapBy('fileName'), restrictedFileList);
  },

  @computed('itemList')
  isMaxFileEditStatusLimit(itemList) {
    return itemList && itemList.length > 100;
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
          if (!STATUS_WITH_REMEDIATION.includes(this.get('formData').fileStatus)) {
            this.set('formData.remediationAction', null);
            this.set('formData.category', null);
          }
          this.onSaveFileStatus(this.get('formData'), callback);
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

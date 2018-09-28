import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  remediationRadioButtons: [
    {
      label: 'investigateFiles.editFileStatus.remediationActionOptions.blockFile',
      value: 'Block',
      selected: true
    },
    {
      label: 'investigateFiles.editFileStatus.remediationActionOptions.blockQuarantineFile',
      value: 'BlockAndQuarantine',
      selected: false
    }
  ],

  fileCategories: [
    'Apt',
    'AttackerTool',
    'GenericMalware',
    'Ransomware',
    'Unidentified'
  ],

  _isSizeExceeded(size) {
    return size > 104857600;
  },

  _isWindowsAgent(item) {
    return item.machineOsType === 'windows';
  },

  @computed('itemList', 'isRemediationAllowed')
  errorDecorator(itemList, isRemediationAllowed) {
    const size = itemList.mapBy('size');
    const sizeExceeds = size.some(this._isSizeExceeded);
    const isNotWindowsAgent = !itemList.every(this._isWindowsAgent);
    let message;
    if (isNotWindowsAgent) {
      message = this.get('i18n').t('investigateFiles.editFileStatus.remediationActionAlert.osNotToBlock');
    } else if (!isRemediationAllowed) {
      message = this.get('i18n').t('investigateFiles.editFileStatus.remediationActionAlert.isSigned');
    } else if (sizeExceeds) {
      message = this.get('i18n').t('investigateFiles.editFileStatus.remediationActionAlert.sizeExceeds');
    }
    return {
      disableBlocking: isNotWindowsAgent || !isRemediationAllowed || sizeExceeds,
      remediationAlert: message
    };
  },

  @computed('data.remediationAction')
  isChecked(remediationAction) {
    return remediationAction === 'Block';
  },

  actions: {
    setFileCategory(category) {
      this.set('data.category', category);
    },

    toggleBlocking() {
      if (this.get('data.remediationAction')) {
        this.set('data.remediationAction', null);
      } else {
        this.set('data.remediationAction', 'Block');
      }
    }
  }
});

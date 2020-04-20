import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  _isSizeExceeded(size) {
    return size > 104857600;
  },

  _isWindowsAgent(item) {
    return item.machineOsType === 'windows';
  },

  @computed('itemList', 'isRemediationAllowed', 'isFloatingOrMemoryDll')
  errorDecorator(itemList, isRemediationAllowed, isFloatingOrMemoryDll) {
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
    } else if (isFloatingOrMemoryDll) {
      message = this.get('i18n').t('investigateFiles.editFileStatus.remediationActionAlert.isFloatingOrMemoryDll');
    }
    return {
      disableBlocking: isNotWindowsAgent || !isRemediationAllowed || sizeExceeds || isFloatingOrMemoryDll,
      remediationAlert: message
    };
  },

  @computed('data.remediationAction')
  isChecked(remediationAction) {
    return remediationAction === 'Block';
  },

  init() {
    this._super(...arguments);
    this.remediationRadioButtons = this.remediationRadioButtons || [
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
    ];

    this.fileCategories = this.fileCategories || [
      'Apt',
      'AttackerTool',
      'GenericMalware',
      'Ransomware',
      'Unidentified'
    ];
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

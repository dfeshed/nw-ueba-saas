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
    'APT',
    'AttackerTool',
    'GenericMalware',
    'Ransom',
    'Unidentified'
  ],

  _isKnownSigner(signer) {
    if (signer) {
      return signer.includes('Microsoft') || signer.includes('RSA');
    }
    return false;
  },

  _isSizeExceeded(size) {
    return size > 104857600;
  },

  @computed('itemList')
  errorDecorator(itemList) {
    const signer = itemList.mapBy('signature.signer');
    const isSigned = signer.some(this._isKnownSigner);
    const size = itemList.mapBy('size');
    const sizeExceeds = size.some(this._isSizeExceeded);
    let message;
    if (isSigned) {
      message = this.get('i18n').t('investigateFiles.editFileStatus.remediationActionAlert.isSigned');
    } else if (sizeExceeds) {
      message = this.get('i18n').t('investigateFiles.editFileStatus.remediationActionAlert.sizeExceeds');
    }
    return {
      disableBlocking: isSigned || sizeExceeds,
      remediationAlert: message
    };
  },

  actions: {
    setFileCategory(category) {
      this.set('data.category', category);
    }
  }
});

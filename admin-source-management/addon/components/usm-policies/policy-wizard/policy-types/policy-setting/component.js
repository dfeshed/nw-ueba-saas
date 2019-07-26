import Component from '@ember/component';
import { computed } from '@ember/object';

const PolicySetting = Component.extend({
  tagName: 'div',
  classNames: ['selected-setting', 'selected-setting-added'],
  classNameBindings: ['selectedSettingClass'],

  selectedSettingClass: computed('selectedSettingId', function() {
    return `${this.selectedSettingId}-setting`;
  }),

  greyOutSetting: computed('selectedSettingId', function() {
    // These four settings should not be greyed out for a default edr policy
    switch (this.selectedSettingId) {
      case 'primaryAddress':
      case 'primaryHttpsBeaconInterval':
      case 'primaryUdpBeaconInterval':
      case 'customConfig':
        return false;
      default:
        return true;
    }
  }),

  // closure action expected to be passed in
  removeFromSelectedSettings: null,
  // setting component name expected to be passed in
  settingComponent: '',
  // setting ID expected to be passed in
  selectedSettingId: '',
  // edrPolicy, windowsLogPolicy, etc... expected to be passed in
  policyType: '',
  // default policy flag expected to be passed in
  isDefaultPolicy: false,
  // setting label expected to be passed in
  label: '',
  // setting label tooltip text expected to be passed in
  tooltip: ''
});

export default PolicySetting;

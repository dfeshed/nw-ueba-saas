import Component from '@ember/component';
import { computed } from '@ember/object';

const PolicySetting = Component.extend({
  tagName: 'div',
  classNames: ['selected-setting', 'selected-setting-added'],
  classNameBindings: ['selectedSettingClass'],

  selectedSettingClass: computed('selectedSettingId', function() {
    return `${this.selectedSettingId}-setting`;
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

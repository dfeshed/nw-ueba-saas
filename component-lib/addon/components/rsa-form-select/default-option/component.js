import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { isBlank } from '@ember/utils';
import layout from './template';

const DefaultOption = Component.extend({
  layout,
  tagName: '',

  // expected to be passed in
  disabled: false,
  disabledTooltip: '',
  optionLabel: '',
  // enabledTooltip: '', // add if/when needed

  // rsa-text-with-tooltip options
  tooltipStyle: 'standard',
  tooltipAlwaysShow: true,
  tooltipCopyText: false, // hide copy button as it currently doesn't work nested in a power-select

  @computed('disabledTooltip')
  hasDisabledTooltip(disabledTooltip) {
    return !isBlank(disabledTooltip);
  }

});

export default DefaultOption;

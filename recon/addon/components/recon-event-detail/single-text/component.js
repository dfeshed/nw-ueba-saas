import Component from 'ember-component';
import SelectionTooltip from './selection-tooltip-mixin';
import layout from './template';

export default Component.extend(SelectionTooltip, {
  classNames: ['rsa-packet', 'rsa-text-entry'],
  classNameBindings: ['packet.side'],
  layout,

  index: null,
  isLog: false,
  packet: null
});

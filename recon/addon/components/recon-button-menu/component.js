import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  tagName: 'ul',
  classNames: ['recon-button-menu'],
  classNameBindings: ['isExpanded:expanded:collapsed', 'menuStyle'],
  attributeBindings: ['style'],
  style: null,
  isExanded: false,
  menuStyle: null
});

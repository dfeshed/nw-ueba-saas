import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  tagName: 'hbox',
  classNames: ['rsa-page-layout'],
  classNameBindings: ['showLeftZone:show-left-zone', 'showRightZone:show-right-zone'],
  showLeftZone: true,
  showRightZone: true,
  onClose: null,

  actions: {
    open(side) {
      if (side === 'left') {
        this.set('showLeftZone', true);
      } else if (side === 'right') {
        this.set('showRightZone', true);
      }
    },
    close(side) {
      if (side === 'left') {
        this.set('showLeftZone', false);
      } else if (side === 'right') {
        this.set('showRightZone', false);
      }
      if (this.get('onClose')) {
        this.onClose(side);
      }
    }
  }
});

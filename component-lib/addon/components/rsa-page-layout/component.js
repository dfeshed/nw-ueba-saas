import Component from '@ember/component';
import layout from './template';

const isSmallScreen = () => {
  const w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
  return w <= 1800;
};

export default Component.extend({
  layout,
  tagName: 'hbox',
  classNames: ['rsa-page-layout'],
  classNameBindings: ['showLeftZone:show-left-zone', 'showRightZone:show-right-zone', 'isRightOverlay'],
  showLeftZone: true,
  showRightZone: true,
  onClose: null,
  isRightOverlay: false,
  actions: {
    open(side) {
      if (isSmallScreen()) {
        this.send('close', side === 'left' ? 'right' : 'left');
      }
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

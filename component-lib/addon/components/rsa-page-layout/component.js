import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  tagName: 'hbox',
  classNames: ['rsa-page-layout'],
  classNameBindings: ['showLeftZone:show-left-zone', 'showRightZone:show-right-zone'],
  showLeftZone: true,
  showRightZone: true,

  actions: {
    open(side) {
      if (side === 'left') {
        this.set('showLeftZone', true);
      } else {
        this.set('showRightZone', true);
      }
    },
    close(side) {
      if (side === 'left') {
        this.set('showLeftZone', false);
      } else {
        this.set('showRightZone', false);
      }
    }
  }
});

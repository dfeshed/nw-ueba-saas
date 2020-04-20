import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  xs: '2',
  tagName: 'vbox',
  classNames: ['rsa-context-panel-header__header-icons'],
  attributeBindings: ['xs'],
  actions: {
    closeAction() {
      if (this.closePanel) {
        this.closePanel();
      }
      if (this.restoreDefault) {
        this.restoreDefault();
      }
    }
  }
});

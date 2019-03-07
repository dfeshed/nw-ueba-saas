import layout from './template';
import Component from '@ember/component';
import { inject as service } from '@ember/service';

export default Component.extend({
  layout,
  eventBus: service(),
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
      this.get('eventBus').trigger('rsa-application-click');
    }
  }
});

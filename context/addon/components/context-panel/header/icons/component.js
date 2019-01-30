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
      this.sendAction('closePanel');
      this.sendAction('restoreDefault');
      this.get('eventBus').trigger('rsa-application-click');
    }
  }
});

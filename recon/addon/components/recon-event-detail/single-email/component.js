import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  isEmailExpanded: true,

  actions: {
    toggleEmailExpansion(isEmailExpanded) {
      this.set('isEmailExpanded', isEmailExpanded);
    }
  }
});

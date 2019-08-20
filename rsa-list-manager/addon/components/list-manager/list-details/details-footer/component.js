import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  tagName: 'footer',
  layout,
  classNames: ['details-footer'],
  updateView: null,

  actions: {

    handleSaveItem() {
      this.get('updateView')('list-view');
    },

    handleCancelItem() {
      this.get('updateView')('list-view');
    }

  }
});

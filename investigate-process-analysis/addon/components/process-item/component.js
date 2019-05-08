import Component from '@ember/component';
import computed from 'ember-computed-decorators';

export default Component.extend({
  classNames: ['process-item'],

  isProcessSelected: false,

  @computed
  processIcon() {
    // TODO when the cons for different processes are introduced, this will be handled.
    return 'cog';
  },

  actions: {

    itemClick(data) {
      this.toggleProperty('isProcessSelected');
      if (this.onSelection) {
        this.onSelection(data);
      }
    }

  }

});
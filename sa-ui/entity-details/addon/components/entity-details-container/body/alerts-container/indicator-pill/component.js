import Component from '@ember/component';

export default Component.extend({
  tagName: '',
  actions: {
    initIndicator(indicatorId) {
      this.get('initIndicator')(indicatorId);
    }
  }
});

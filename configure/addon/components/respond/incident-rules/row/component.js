import Component from '@ember/component';

export default Component.extend({
  tagName: null,

  onRowClick() {},
  onCheckboxClick() {},

  click() {
    this.onRowClick(...arguments);
    return false;
  },

  actions: {
    handleCheckboxClick(rule) {
      this.onCheckboxClick(rule);
    }
  }
});

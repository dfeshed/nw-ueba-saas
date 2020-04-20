import Component from '@ember/component';

export default Component.extend({
  classNames: ['list-filter', 'column-group-column-filter'],

  // text to display in the filter
  filterText: undefined,

  // action to call when text changes
  filterTextUpdated: undefined,

  // should the text be selected for easy removal?
  shouldSelectTextForRemoval: undefined,

  didUpdateAttrs() {
    if (this.get('shouldSelectTextForRemoval') === true) {
      const ele = this.element.querySelector('input');
      ele.focus();
      ele.select();
    }
  },

  actions: {
    handleInput(e) {
      this.get('filterTextUpdated')(e.target.value);
    },

    resetFilter() {
      this.get('filterTextUpdated')('');
    }
  }
});
import Component from '@ember/component';

export default Component.extend({
  classNames: ['list-filter', 'column-group-column-filter'],

  // text to display in the filter
  filterText: undefined,

  // action to call when text changes
  filterTextUpdated: undefined,

  actions: {
    handleInput(e) {
      this.get('filterTextUpdated')(e.target.value);
    },

    resetFilter() {
      this.get('filterTextUpdated')('');
    }
  }
});
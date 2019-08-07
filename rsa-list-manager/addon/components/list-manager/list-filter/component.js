import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  classNames: ['list-filter'],
  listName: null,
  originalList: null,
  filterAction: null,
  updateFilteredList: null,
  filterText: '',

  didInsertElement() {
    this.initializeElement();
  },

  initializeElement() {
    this.set('filterText', '');
    this.get('updateFilteredList')(this.get('originalList'));
  },

  filterList(value) {
    const originalList = this.get('originalList');

    let filteredList = originalList;

    if (this.get('filterAction')) {
      filteredList = this.get('filterAction')(value);
    } else {
      if (originalList) {
        filteredList = originalList.filter((item) => item.name.toLowerCase().includes(value.toLowerCase()));
      }
    }
    this.get('updateFilteredList')(filteredList);
  },

  @computed('listName')
  filterPlaceholder(listName) {
    return `Filter ${listName.toLowerCase()}`;
  },

  actions: {

    handleInput(e) {
      const { value } = e.target;
      this.set('filterText', value);
      this.filterList(value);
    },

    resetFilter() {
      this.initializeElement();
    }
  }
});

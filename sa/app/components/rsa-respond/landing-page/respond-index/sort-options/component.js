/**
 * @file Sort Options component.

 * This component is used to display a customizable sort menu tailored for Incident model. Component displays
 * a label, sort drop down menu, and a button for sort direction (asc/desc).
 * @public
 */
import Ember from 'ember';
import computed,  { readOnly } from 'ember-computed-decorators';

const {
  Component
} = Ember;

export default Component.extend({

  classNames: 'sort-options',
  label: '',
  defaultSortOption: '', // Property used to display default sort order
  sortOptions: [],
  isDesc: true,
  viewType: '', // Property used to determine which incidents need to be sorted

  @readOnly
  @computed('isDesc')
  direction: (isDesc) => (isDesc ? 'desc' : 'asc'),

  /*
   * Computed property that renders default sort option and handles sort selection
   * changes by updating the current sort and invoking sort action.
   */
  @computed('defaultSortOption')
  selectedSortOption: {
    get: (defaultSortOption) => [defaultSortOption],

    set(sortOptions) {
      this.sendAction('sortAction', sortOptions[0], this.get('direction'), this.get('viewType'));
      return sortOptions;
    }
  },

  actions: {

    /*
     * Handle direction change of the sort (asc / desc)
     */
    toggleSortDir() {
      this.toggleProperty('isDesc');
      let options = this.get('selectedSortOption');
      this.sendAction('sortAction', options[0], this.get('direction'), this.get('viewType'));
    }

  }

});
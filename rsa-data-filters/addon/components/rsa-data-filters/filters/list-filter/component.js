import Component from '@ember/component';
import layout from './template';
import { set, computed } from '@ember/object';

export default Component.extend({
  layout,

  classNames: ['list-filter'],

  filterValue: computed('filterOptions', {
    get() {
      const { filterValue, listOptions } = this.get('filterOptions');
      return listOptions.map((opt) => {
        const selected = filterValue && filterValue.includes(opt.name);
        return { ...opt, selected };
      });
    },

    set(key, value) {
      return value;
    }
  }),

  didReceiveAttrs() {
    this._super(...arguments);
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
    }
  },


  actions: {
    toggleSelected(option, event) {
      if (event.charCode !== undefined && event.charCode !== 32) {
        return true;
      }
      const { selected } = option;
      const { name } = this.get('filterOptions');
      const onChange = this.get('onChange');

      set(option, 'selected', !selected);

      const selectedValues = this.get('filterValue').filterBy('selected', true);
      const value = selectedValues.mapBy('name');

      if (onChange) {
        onChange({ name, operator: 'IN', value });
      }
      event.preventDefault();
    }
  }
});

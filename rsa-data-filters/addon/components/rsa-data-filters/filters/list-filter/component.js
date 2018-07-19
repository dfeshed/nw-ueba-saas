import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { set } from '@ember/object';

export default Component.extend({
  layout,

  classNames: ['list-filter'],

  @computed('filterOptions')
  filterValue(options) {
    const { filterValue, listOptions } = options;
    return listOptions.map((opt) => {
      const selected = filterValue && filterValue.includes(opt.name);
      return { ...opt, selected };
    });
  },

  actions: {
    toggleSelected(option) {
      const { selected } = option;
      const { name } = this.get('filterOptions');
      const onChange = this.get('onChange');

      set(option, 'selected', !selected);

      const selectedValues = this.get('filterValue').filterBy('selected', true);
      const value = selectedValues.mapBy('name');

      if (onChange) {
        onChange({ name, operator: 'IN', value });
      }
    }
  }
});

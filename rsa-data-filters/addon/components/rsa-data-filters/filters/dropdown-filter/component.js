import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';
import computed, { alias } from 'ember-computed-decorators';

export default Component.extend({
  layout,

  classNames: ['dropdown-filter'],

  defaults: {
    multiSelect: false,
    searchEnabled: false,
    filterValue: []
  },

  @alias('options.multiSelect')
  isMultiSelect: false,

  @computed('options', 'isMultiSelect')
  filterValue: {
    get() {
      const { filterValue, listOptions = [] } = this.get('options');
      const values = listOptions.filter((opt) => {
        return filterValue && filterValue.includes(opt.name);
      });
      return this.get('isMultiSelect') ? values : values[0];
    },

    set(key) {
      return key;
    }
  },

  didReceiveAttrs() {
    this._super(...arguments);
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
    }
  },

  init() {
    this._super(arguments);
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    this.set('options', options);
  },

  actions: {
    changeOption(option) {
      const { name } = this.get('filterOptions');
      const onChange = this.get('onChange');
      const isMultiSelect = this.get('isMultiSelect');

      let value;

      if (option) {
        if (isMultiSelect) {
          value = option.mapBy('name');
        } else {
          value = [option.name];
        }
      }

      this.set('filterValue', option);

      if (onChange) {
        onChange({ name, operator: 'IN', value });
      }
    }
  }
});

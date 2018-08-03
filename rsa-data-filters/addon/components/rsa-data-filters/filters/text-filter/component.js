import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';
import { isEmpty } from '@ember/utils';
import { computed } from '@ember/object';

export default Component.extend({

  layout,

  classNames: ['text-filter'],

  defaults: {
    maxLength: 256,
    operators: [
      { type: 'IN', label: 'Equals' },
      { type: 'LIKE', label: 'Contains' }
    ],
    filterValue: {
      operator: 'IN',
      value: []
    }
  },

  filterValue: computed('options', {

    get() {
      const { filterValue: { operator, value }, operators } = this.get('options');
      const selectedOperator = operators.findBy('type', operator);
      const val = value.join('||');
      return { operator: selectedOperator, value: val };
    },

    set(key, value) {
      return value;
    }
  }),

  init() {
    this._super(arguments);
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    const { filterValue } = options;
    this.set('value', filterValue);
    this.set('options', options);
  },

  didReceiveAttrs() {
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
    }
  },

  _onFilterChange() {
    const { filterValue: { value, operator: { type } }, options: { name }, onChange } = this.getProperties('filterValue', 'options', 'onChange');
    if (onChange) {
      let val = [];
      if (!isEmpty(value)) {
        val = value.split('||');
      }
      onChange({ name, operator: type, value: val });
    }
  },

  actions: {
    onInputFocusOut(e) {
      const { value } = e.target;
      this.set('filterValue.value', value);
      this._onFilterChange();
    },

    changeOperator(option) {
      this.set('filterValue.operator', option);
      this._onFilterChange();
    }
  }
});

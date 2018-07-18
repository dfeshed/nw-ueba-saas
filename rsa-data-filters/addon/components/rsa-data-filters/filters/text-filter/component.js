import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';
import computed from 'ember-computed-decorators';

export default Component.extend({

  layout,

  classNames: ['text-filter'],

  _tempValue: {},

  defaults: {
    maxLength: 256,
    operators: [
      { type: 'IN', label: 'Equals' },
      { type: 'LIKE', label: 'Contains' }
    ],
    filterValue: {
      operator: 'IN',
      value: null
    }
  },

  @computed('options')
  filterValue(options) {
    const { filterValue: { operator, value }, operators } = options;
    const selectedOperator = operators.findBy('type', operator);
    this.set('_tempValue', { value, operator: selectedOperator.type }); // Set the default values to temp
    return { selectedOperator, value };
  },

  init() {
    this._super(arguments);
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    this.set('options', options);

  },

  _onFilterChange() {
    const { _tempValue: { value, operator }, options: { name }, onChange } = this.getProperties('_tempValue', 'options', 'onChange');
    if (onChange) {
      onChange({ name, operator, value });
    }
  },

  actions: {
    onInputFocusOut(e) {
      const { value } = e.target;
      this.set('_tempValue.value', value);
      this._onFilterChange();
    },

    changeOperator(option) {
      this.set('_tempValue.operator', option.type);
      this._onFilterChange();
    }
  }
});

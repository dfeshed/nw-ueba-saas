import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';
import { isEmpty } from '@ember/utils';
import { computed } from '@ember/object';
import { debounce } from '@ember/runloop';

export default Component.extend({

  layout,

  classNames: ['text-filter'],

  defaults: {
    maxLength: 256,
    filterOnBlur: false,
    operators: [
      { type: 'IN', label: 'Equals' },
      { type: 'LIKE', label: 'Contains' },
      { type: 'EQUAL', label: 'Equals' }
    ],
    filterValue: {
      operator: 'IN',
      value: []
    }
  },

  isError: false,

  errorMessage: '',

  filterValue: computed('options', {

    get() {
      const { filterValue: { operator, value }, operators, placeholder } = this.get('options');
      const selectedOperator = operators.findBy('type', operator);
      const val = value.join('||');
      const placeholderText = placeholder ? placeholder : 'Enter value';
      return { operator: selectedOperator, value: val, placeholderText };
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
    if (this.get('validations') === undefined) {
      this.validations = {};
    }
  },

  didReceiveAttrs() {
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
      this.set('isError', false);
      this.set('errorMessage', '');
    }
  },

  _validate() {
    let property;
    const value = this.get('filterValue.value');

    if (isEmpty(value.trim())) {
      return { isValid: true };
    }

    const operator = this.get('filterValue.operator.type');
    const validations = this.get('options.validations');
    for (property in validations) {
      const validatorObject = validations[property];
      if (validatorObject.constructor === Object) {
        const { validator, message, exclude = [] } = validatorObject;
        if (!exclude.includes(operator)) {
          const isInvalid = validator(value);
          if (isInvalid) {
            return {
              isValid: false,
              message
            };
          }
        }
      }
    }
    return { isValid: true };
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

  _handleFilterChanged(value) {
    this.set('filterValue.value', value);
    const { isValid, message } = this._validate();
    if (isValid) {
      this.set('isError', false);
      this._onFilterChange();
    } else {
      this.set('isError', true);
      this.set('errorMessage', message);
    }
  },

  actions: {
    handleKeyUp(value = '') {
      debounce(this, this._handleFilterChanged, value, 600);
    },

    changeOperator(option) {
      this.set('filterValue.operator', option);
      if (!this.get('isError')) {
        this._onFilterChange();
      }
    }
  }
});

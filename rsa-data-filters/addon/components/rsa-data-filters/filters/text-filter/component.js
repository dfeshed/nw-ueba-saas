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

  isError: false,

  errorMessage: '',

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
    if (this.get('validations') === undefined) {
      this.validations = {};
    }
  },

  didReceiveAttrs() {
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
    }
  },

  _validate() {
    let property;
    const value = this.get('filterValue.value');
    const validations = this.get('options.validations');
    for (property in validations) {
      const validatorObject = validations[property];
      if (validatorObject.constructor === Object) {
        const { validator, message } = validatorObject;
        const isInvalid = validator(value);
        if (isInvalid) {
          return {
            isValid: false,
            message
          };
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

  _handeFilterChanged() {
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
    onEnter(value) {
      this.set('filterValue.value', value);
      this._handeFilterChanged();
    },
    onInputFocusOut(e) {
      const { value } = e.target;
      this.set('filterValue.value', value);
      this._handeFilterChanged();
    },

    changeOperator(option) {
      this.set('filterValue.operator', option);
      this._onFilterChange();
    }
  }
});

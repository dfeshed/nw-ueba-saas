import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';
import computed from 'ember-computed-decorators';

export default Component.extend({

  layout,

  classNames: ['number-filter'],

  defaults: {
    operators: [
      { label: 'Equals', type: 'EQUAL' },
      { label: 'Greater Than', type: 'GREATER_THAN' },
      { label: 'Less Than', type: 'LESS_THAN' },
      { label: 'Between', type: 'BETWEEN' }
    ],
    filterValue: {
      operator: 'EQUAL',
      value: []
    }
  },

  _tempValue: {},

  @computed('options')
  filterValue(options) {
    const { filterValue: { operator, value, unit }, operators, units } = options;
    const selectedOperator = operators.findBy('type', operator);
    let selectedUnit = null;

    if (units && units.length) {
      selectedUnit = unit ? units.findBy('type', unit) : units[0];
      this.set('_tempValue', { value, operator: selectedOperator.type, unit: selectedUnit.type });
    } else {
      this.set('_tempValue', { value, operator: selectedOperator.type });
    }

     // Set the default values to temp
    return { selectedOperator, value, selectedUnit };
  },

  @computed('options.units')
  hasUnits(units) {
    return units && units.length;
  },

  @computed('_tempValue.operator')
  isOperatorBetween(operator) {
    return 'BETWEEN' === operator;
  },

  init() {
    this._super(arguments);
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    this.set('options', options);
  },

  _onFilterChange() {
    const onChange = this.get('onChange');
    const filterValue = this.get('_tempValue');
    if (onChange) {
      onChange(filterValue);
    }
  },
  actions: {
    onInputFocusOut(e) {
      const isOperatorBetween = this.get('isOperatorBetween');
      if (isOperatorBetween) {
        const start = this.element.querySelector('.number-input.start input').value;
        const end = this.element.querySelector('.number-input.end input').value;
        this.set('_tempValue.value', [start, end]);
      } else {
        this.set('_tempValue.value', [e.target.value]);
      }

      this._onFilterChange();
    },

    changeOperator(option) {
      this.set('_tempValue.operator', option.type);

      this._onFilterChange();
    },

    chageUnit(option) {
      this.set('_tempValue.unit', option.type);
      this._onFilterChange();
    }
  }
});

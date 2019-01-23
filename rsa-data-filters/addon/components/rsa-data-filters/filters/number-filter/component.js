import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';
import computed from 'ember-computed-decorators';
import { debounce, next } from '@ember/runloop';

export default Component.extend({

  layout,

  classNames: ['number-filter'],

  defaults: {
    filterOnBlur: false,
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

  oldValue: {
    operator: 'EQUAL',
    value: []
  },

  @computed('options')
  filterValue: {
    get() {
      const { filterValue: { operator, value, unit }, operators, units } = this.get('options');
      const selectedOperator = operators.findBy('type', operator);
      let selectedUnit = null;

      if (units && units.length) {
        selectedUnit = unit ? units.findBy('type', unit) : units[0];
      }
      // Set the default values to temp
      return { operator: selectedOperator, value: [ ...value ], unit: selectedUnit };
    },

    set(key, value) {
      return value;
    }

  },

  @computed('options.units')
  hasUnits(units) {
    return units && units.length;
  },

  @computed('filterValue.operator')
  isOperatorBetween(operator) {
    return 'BETWEEN' === (operator && operator.type);
  },

  init() {
    this._super(arguments);
    const { operators, filterValue } = this.get('filterOptions');
    if (operators && !filterValue) {
      this.set('defaults.filterValue', { operator: operators[0].type, value: [] });
    }
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    this.set('options', options);
  },


  didReceiveAttrs() {
    this._super(...arguments);
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
    }
  },

  _onFilterChange() {
    const { onChange, filterValue, options: { name } } = this.getProperties('onChange', 'filterValue', 'options');
    const { operator, value, unit } = filterValue;
    if (onChange) {
      onChange({ operator: operator.type, value, unit: unit ? unit.type : null, name });
    }
  },

  _handleFilterChange(value) {
    const isOperatorBetween = this.get('isOperatorBetween');
    if (isOperatorBetween) {
      const start = this.element.querySelector('.number-input.start input').value * 1;
      const end = this.element.querySelector('.number-input.end input').value * 1;
      this.set('filterValue.value', [start, end]);
    } else {
      this.set('filterValue.value', [value * 1]);
    }
    next(() => {
      this._onFilterChange();
    });
  },

  actions: {
    handleKeyUp(value = '') {
      debounce(this, this._handleFilterChange, value, 600);
    },

    changeOperator(option) {
      this.set('filterValue.operator', option);
      this._onFilterChange();
    },

    chageUnit(option) {
      this.set('filterValue.unit', option);
      this._onFilterChange();
    }
  }
});

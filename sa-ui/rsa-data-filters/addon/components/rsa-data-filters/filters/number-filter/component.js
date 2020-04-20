import classic from 'ember-classic-decorator';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';
import { debounce, next } from '@ember/runloop';

@classic
@templateLayout(layout)
@classNames('number-filter')
export default class NumberFilter extends Component {
  @computed('options')
  get filterValue() {
    const { filterValue: { operator, value, unit }, operators, units } = this.get('options');
    const selectedOperator = operators.findBy('type', operator);
    let selectedUnit = null;

    if (units && units.length) {
      selectedUnit = unit ? units.findBy('type', unit) : units[0];
    }
    // Set the default values to temp
    return { operator: selectedOperator, value: [ ...value ], unit: selectedUnit };
  }

  set filterValue(value) {
    return value;
  }

  @computed('options.units')
  get hasUnits() {
    return this.options?.units && this.options?.units.length;
  }

  @computed('filterValue.operator')
  get isOperatorBetween() {
    return 'BETWEEN' === (this.filterValue?.operator && this.filterValue?.operator.type);
  }

  init() {
    super.init(arguments);
    this.defaults = this.defaults || {
      filterOnBlur: false,
      isDecimalAllowed: true,
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
    };
    this.oldValue = this.oldValue || { operator: 'EQUAL', value: [] };
    const { operators, filterValue } = this.get('filterOptions');
    if (operators && !filterValue) {
      this.set('defaults.filterValue', { operator: operators[0].type, value: [] });
    }
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    this.set('options', options);
  }

  didReceiveAttrs() {
    super.didReceiveAttrs(...arguments);
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
    }
  }

  _onFilterChange() {
    const { onChange, filterValue, options: { name } } = this.getProperties('onChange', 'filterValue', 'options');
    const { operator, value, unit } = filterValue;
    if (onChange) {
      onChange({ operator: operator.type, value, unit: unit ? unit.type : null, name });
    }
  }

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
  }

  @action
  handleKeyUp(value = '') {
    debounce(this, this._handleFilterChange, value, 600);
  }

  @action
  handleKeyPress(val, event) {
    if (event.charCode === 46 && !this.get('filterOptions.isDecimalAllowed')) {
      event.preventDefault();
    }
  }

  @action
  changeOperator(option) {
    this.set('filterValue.operator', option);
    this._onFilterChange();
  }

  @action
  chageUnit(option) {
    this.set('filterValue.unit', option);
    this._onFilterChange();
  }
}
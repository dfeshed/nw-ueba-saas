import { connect } from 'ember-redux';
import { isEmpty } from 'ember-utils';
import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';

import layout from './template';
import { convertFromBytes, convertToBytes } from './util';
import FilterMixin from 'investigate-files/mixins/content-filter-mixin';
import { updateFilter } from 'investigate-files/actions/data-creators';

const FILE_SIZE_UNITS = ['bytes', 'KB', 'MB', 'GB' ];

const RESTRICTION_TYPES = [
  { name: 'investigateFiles.filter.restrictionType.equals', type: 'EQUAL' },
  { name: 'investigateFiles.filter.restrictionType.moreThan', type: 'GREATER_THAN' },
  { name: 'investigateFiles.filter.restrictionType.lessThan', type: 'LESS_THAN' },
  { name: 'investigateFiles.filter.restrictionType.between', type: 'BETWEEN' }
];

const RESTRICTION_TYPES_BY_TYPE = {};

RESTRICTION_TYPES.forEach((t) => RESTRICTION_TYPES_BY_TYPE[t.type] = t);

const EXPONENT_CHARS = ['e', 'E', '+', '-'];

const dispatchToActions = {
  updateFilter
};

/**
 * Filter for number data type. Displays the list supported restriction types in dropdown and text box to enter value
 * @public
 */
const NumberFilter = Component.extend(FilterMixin, {

  layout,

  eventBus: service(),

  classNames: ['number-filter'],

  /**
   * Property used for between selector
   * @public
   */
  start: null,

  /**
   * Property used for between selector
   * @public
   */
  end: null,

  /**
   * Units for memory
   * @public
   */
  units: FILE_SIZE_UNITS,

  /**
   * Default memory size
   * @public
   * @property
   */
  selectedUnit: 'KB',

  /**
   * Currently selected restriction type
   * @public
   * @property
   */
  restrictionType: null,

  /**
   * Dropdown options for number unit selection
   * @public
   */
  @computed('config.options')
  dropDownValues(options) {
    return options || RESTRICTION_TYPES;
  },

  /**
   * Creates the label for the filter with value and restriction type
   * @param expression
   * @returns {string}
   * @public
   */
  @computed('config.expression')
  filterLabel(expression) {
    const filterName = this.get('i18n').t(this.get('config.label'));
    if (expression && expression.propertyValues) {
      const { restrictionType, propertyName, propertyValues: inputValue } = expression;
      const { name } = RESTRICTION_TYPES_BY_TYPE[restrictionType];
      let values = inputValue;

      if (propertyName === 'size') {
        values = convertFromBytes(inputValue);
      }
      const translated = this.get('i18n').t(name);
      const unit = values[0].unit || '';
      return restrictionType === 'BETWEEN' ?
        `${filterName}: ${values[0].value}-${values[1].value} ${unit}` :
        `${filterName}: ${translated} ${values[0].value} ${unit}`;
    }
    return `${filterName}: All`;
  },

  /**
   * Parse the given expression for display
   * @param expression
   * @public
   */
  parseExpression(expression) {
    if (expression && expression.propertyValues) {
      const { restrictionType, propertyValues: value } = expression;
      this.set('restrictionType', RESTRICTION_TYPES_BY_TYPE[restrictionType]);
      const convertedValue = convertFromBytes(value);
      if (value && value.length > 1) { // If length > 1 then set the start and end
        this.set('start', convertedValue[0].value);
        this.set('end', convertedValue[1].value);
        this.set('selectedUnit', convertedValue[0].unit);
      } else {
        this.set('value', convertedValue[0].value);
        this.set('selectedUnit', convertedValue[0].unit);
      }
    }
  },

  init() {
    this._super(...arguments);
    const { expression } = this.get('config');
    this.parseExpression(expression);
  },

  actions: {
    keyDown(e) {
      // Number field allows to input 'e', 'E', '-', '+' to support exponent numbers. Exclude them
      return !EXPONENT_CHARS.includes(e.key);
    },

    onFilterUpdate() {
      const {
        start,
        end,
        selectedUnit,
        value,
        config: { propertyName },
        restrictionType: { type }
      } = this.getProperties('restrictionType', 'config', 'start', 'end', 'selectedUnit', 'value');
      let propertyValues = null;
      if ((value && value.length) || (!isEmpty(start) && !isEmpty(end))) {
        if (propertyName === 'entropy' || propertyName === 'size') { // for entropy parse Float
          propertyValues = type === 'BETWEEN' ? [{ value: parseFloat(start) }, { value: parseFloat(end) }] : [{ value: parseFloat(value) }];
        }
      } else {
        this.set('restrictionType', null);
      }
      // From the ui user has selected the memory unit convert that into bytes
      if (propertyName === 'size' && propertyValues) {
        propertyValues = convertToBytes(selectedUnit, propertyValues);
      }
      this.send('updateFilter', { propertyName, restrictionType: type, propertyValues });
    }
  }
});

export default connect(undefined, dispatchToActions)(NumberFilter);
import { connect } from 'ember-redux';
import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';
import FilterMixin from 'investigate-hosts/mixins/content-filter-mixins';
import set from 'ember-metal/set';
import { prepareExpressionProperty } from './utils';

import {
  updateFilter
} from 'investigate-hosts/actions/data-creators/filter';


const dispatchToActions = {
  updateFilter
};


// Restriction/Operator type for text filter
const RESTRICTION_TYPES = [
  {
    type: 'LESS_THAN',
    label: 'investigateHosts.hosts.restrictionTypeOptions.LESS_THAN'
  },
  {
    type: 'GREATER_THAN',
    label: 'investigateHosts.hosts.restrictionTypeOptions.GREATER_THAN'
  },
  {
    type: 'BETWEEN',
    label: 'investigateHosts.hosts.restrictionTypeOptions.BETWEEN'
  }
];

const RESTRICTION_TYPES_BY_TYPE = {};

RESTRICTION_TYPES.forEach((t) => RESTRICTION_TYPES_BY_TYPE[t.type] = t);

/**
 * Filter for array data type. Displays the list of values as list with checkbox.
 * On selecting the checkbox content will be filtered
 * @public
 */
const DateTimeFilter = Component.extend(FilterMixin, {

  eventBus: service(),

  classNames: ['datetime-filter'],

  showListOptions: true,

  currentDate: new Date(),

  showRestrictionType: true,

  showRangeFilter: false,

  restrictionTypes: RESTRICTION_TYPES,

  selectedDateRangeOption: {},

   /**
   * Restriction type for the list filter
   * @public
   */
  restrictionType: RESTRICTION_TYPES[1],

  /**
   * Prepared List option for displaying
   * @param options
   * @param selections
   * @returns {Array}
   * @public
   */
  @computed('config.options', 'config.selected')
  listOptions(options, selections) {
    return options.map((item) => {
      const listOption = { name: item.label, id: item.id, selected: item.selected };
      if (item.label === selections[0] || item.selected) {
        this.set('selectedDateRangeOption', listOption);
      }
      return listOption;
    });
  },

  /**
   * Toggle date range filter based on config flag
   * @param showDateRange
   * @returns {Array}
   * @public
   */
  @computed('config.showDateRange')
  restrictionList(showDateRange) {
    const types = RESTRICTION_TYPES;
    if (!showDateRange) {
      types.splice(2, 1);
    }
    return types;
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
      const { propertyValues } = expression;
      const values = propertyValues.map((item) => item.displayValue);

      this.set('restrictionType', RESTRICTION_TYPES_BY_TYPE[expression.restrictionType]);
      if (expression.isCustom) {
        this.set('isCustomChecked', true);
        if (expression.restrictionType === 'GREATER_THAN' || expression.restrictionType === 'LESS_THAN') {
          this.set('dateValue', expression.propertyValues[0].value);
        } else if (expression.restrictionType === 'BETWEEN') {
          this.set('dateStartValue', expression.propertyValues[0].value);
          this.set('dateEndValue', expression.propertyValues[1].value);
        }
      } else {
        this.set('isCustomChecked', false);
      }

      return `${filterName}: ${values.join(',')}`;
    }
    return `${filterName}: All`;
  },

  /**
   * Need to adjust the width of the tether panel as label content changes.
   * @public
   */
  @computed('filterLabel')
  anchorWidth() {
    return this.$().width();
  },

  /**
   * Parse the given expression for display
   * @param expression
   * @public
   */
  parseExpression(expression) {
    if (expression && expression.propertyValues) {
      const { propertyValues } = expression;
      this.set('config.selected', propertyValues.mapBy('displayValue'));
    } else {
      this.set('config.selected', []);
    }
  },

  init() {
    this._super(...arguments);
    const { expression } = this.get('config');
    this.parseExpression(expression);
  },

  actions: {

    /**
     * On check box selection sends all the selected options to parent
     * @param option
     * @public
     */
    onSelection(option) {
      const { selected } = option;
      set(option, 'selected', !selected);
      const {
        config: { propertyName },
        restrictionType: { type }
      } = this.getProperties('config', 'restrictionType');

      if (option.id === 'Custom') {
        this.set('showListOptions', false);
      } else {
        const values = [{ value: option.id, displayValue: option.name }];
        this.send('updateFilter', { propertyName, restrictionType: type, isCustom: false, propertyValues: prepareExpressionProperty(values) });
      }
    },
    onFilterUpdate() {
      let propertyValues = null;
      const {
        dateStartValue,
        dateEndValue,
        restrictionType: { type },
        dateValue,
        config: { propertyName },
        isCustomChecked,
        selectedDateRangeOption
      } = this.getProperties('config', 'dateValue', 'dateStartValue', 'dateEndValue', 'restrictionType', 'isCustomChecked', 'selectedDateRangeOption');

      if (!isCustomChecked) {
        const values = [{ value: selectedDateRangeOption.id, displayValue: selectedDateRangeOption.name }];
        this.send('updateFilter', { propertyName, restrictionType: type, isCustom: false, propertyValues: prepareExpressionProperty(values) });
      } else {
        if (type === 'BETWEEN') {
          propertyValues = [
            { value: dateStartValue, displayValue: new Date(dateStartValue).toString() },
            { value: dateEndValue, displayValue: new Date(dateEndValue).toString() }
          ];
        } else {
          propertyValues = [{ value: dateValue, displayValue: new Date(dateValue).toString() }];
        }

        this.send('updateFilter', { propertyName, restrictionType: type, isCustom: true, propertyValues: prepareExpressionProperty(propertyValues) });
      }

    },
    toggleIsChecked() {
      this.toggleProperty('isCustomChecked');
    },
    onChange(option) {
      if (option === 'EQUAL') {
        this.set('showRangeFilter', false);
      } else {
        this.set('showRangeFilter', true);
      }
    }
  }
});
export default connect(undefined, dispatchToActions)(DateTimeFilter);

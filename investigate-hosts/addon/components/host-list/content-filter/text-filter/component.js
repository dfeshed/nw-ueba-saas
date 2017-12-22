import { connect } from 'ember-redux';
import { isEmpty } from 'ember-utils';
import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';
import FilterMixin from 'investigate-hosts/mixins/content-filter-mixins';

import {
  updateFilter
} from 'investigate-hosts/actions/data-creators/filter';

// Restriction/Operator type for text filter
const RESTRICTION_TYPES = [
  {
    type: 'EQUAL',
    label: 'investigateHosts.hosts.restrictionTypeOptions.EQUALS'
  },
  {
    type: 'LIKE',
    label: 'investigateHosts.hosts.restrictionTypeOptions.CONTAINS'
  }
];

const RESTRICTION_TYPES_BY_TYPE = {};

RESTRICTION_TYPES.forEach((t) => RESTRICTION_TYPES_BY_TYPE[t.type] = t);

const dispatchToActions = {
  updateFilter
};

/**
 * Adds the appropriate date value to the property value
 * @param {*} propertyValues
 * @public
 */
const preparePropertyValues = (propertyValues) => {
  propertyValues = propertyValues.split('||');
  return propertyValues.map((value) => {
    // Validating the empty strings in the property values.
    if (/^ *$/.test(value)) {
      value = 'error';
    }

    return { value: value.trim() };
  });
};

const TextFilter = Component.extend(FilterMixin, {

  classNames: ['text-filter'],

  eventBus: service(),

  showRestrictionType: true,

  isError: false,

  FILTER_SEPERATOR: '||',

  /**
   * List of supported operators
   * @type []
   * @default `RESTRICTION_TYPES`
   * @public
   */
  restrictionTypes: RESTRICTION_TYPES,

  /**
   * Extract the restriction type from the expression. If expression is null set default value 'IN'
   * @param expression
   * @returns {*}
   * @public
   */
  @computed('config.expression')
  restrictionType(expression) {
    if (expression && expression.propertyValues && expression.restrictionType != 'IN') {
      return expression.restrictionType;
    } else if (expression && expression.propertyValues && expression.restrictionType == 'IN') {
      return 'EQUAL';
    }
    return 'LIKE';
  },

  /**
   * Extract the value from the expression, if expression is null set it ''
   * @param expression
   * @returns {*}
   * @public
   */
  @computed('config.expression')
  value(expression) {
    if (expression && expression.propertyValues) {
      const { propertyValues } = expression;
      const values = propertyValues.map((item) => item.value);
      return `${values.join(this.FILTER_SEPERATOR)}`;
    }
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
    let label;
    if (expression && expression.propertyValues) {
      const { restrictionType, propertyValues: [{ value: inputValue }] } = expression;
      if (restrictionType !== 'IN') {
        label = RESTRICTION_TYPES_BY_TYPE[restrictionType].label;
        return `${filterName}: ${this.get('i18n').t(label)} ${inputValue}`;
      } else if (restrictionType === 'IN') {
        const values = expression.propertyValues.map((item) => item.value);
        return `${filterName}: ${values.join(this.FILTER_SEPERATOR)}`;
      }
    }
    return `${filterName}: All`;
  },

  actions: {
    /**
     * Action to handle the update button click. If value is empty then changeAction will be called with
     * propertyValues as null. Setting propertyValues to null, so that this filter is not included in the final query.
     * @public
     */
    onUpdate() {
      const {
        config: { propertyName },
        restrictionType,
        value
      } = this.getProperties('config', 'restrictionType', 'value');

      const propertyValues = value && !isEmpty(value) ? preparePropertyValues(value) : [];

      const errors = propertyValues.filter((o) => o.value === 'error');
      if (propertyValues.length <= 0 || errors.length) {
        this.set('isError', true);
      } else {
        this.set('isError', false);
        const restrictionTypeUpdated = propertyValues.length > 1 ? 'IN' : restrictionType;

        this.send('updateFilter', { restrictionType: restrictionTypeUpdated, propertyName, propertyValues });
      }
    }
  }
});

export default connect(undefined, dispatchToActions)(TextFilter);

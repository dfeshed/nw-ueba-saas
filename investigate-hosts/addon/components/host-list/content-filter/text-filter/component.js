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


const TextFilter = Component.extend(FilterMixin, {

  classNames: ['text-filter'],

  eventBus: service(),

  showRestrictionType: true,

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
    return expression && expression.propertyValues ? expression.restrictionType : 'LIKE';
  },

  /**
   * Extract the value from the expression, if expression is null set it ''
   * @param expression
   * @returns {*}
   * @public
   */
  @computed('config.expression')
  value(expression) {
    return expression && expression.propertyValues ? expression.propertyValues[0].value : '';
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
      const { restrictionType, propertyValues: [{ value: inputValue }] } = expression;
      const { label } = RESTRICTION_TYPES_BY_TYPE[restrictionType];
      return `${filterName}: ${this.get('i18n').t(label)} ${inputValue}`;
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
      const propertyValues = value && !isEmpty(value) ? [{ value }] : null;
      this.send('updateFilter', { restrictionType, propertyName, propertyValues });
    }
  }
});

export default connect(undefined, dispatchToActions)(TextFilter);

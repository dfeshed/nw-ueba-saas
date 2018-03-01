import { connect } from 'ember-redux';
import { isEmpty } from '@ember/utils';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

import FilterMixin from 'investigate-files/mixins/content-filter-mixin';
import layout from './template';
import { updateFilter } from 'investigate-files/actions/data-creators';
import { evaluateTextAgainstRegEx } from './utils';

// Restriction/Operator type for text filter
const RESTRICTION_TYPES = [
  {
    type: 'IN',
    label: 'investigateFiles.filter.restrictionType.equals'
  },
  {
    type: 'LIKE',
    label: 'investigateFiles.filter.restrictionType.contains'
  }
];

const RESTRICTION_TYPES_BY_TYPE = {};

RESTRICTION_TYPES.forEach((t) => RESTRICTION_TYPES_BY_TYPE[t.type] = t);

const dispatchToActions = {
  updateFilter
};

const TextFilter = Component.extend(FilterMixin, {
  layout,

  classNames: ['text-filter'],

  eventBus: service(),

  showRestrictionType: true,

  errorMessage: '',

  /**
   * List of supported operators
   * @type []
   * @default `RESTRICTION_TYPES`
   * @public
   */
  restrictionTypes: RESTRICTION_TYPES,

  /**
   * Extract the restriction type from the expression. If expression is null set default value 'LiKE'
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
        config: { propertyName, filterType, invalidError = 'invalidCharacters' },
        restrictionType,
        value
      } = this.getProperties('config', 'restrictionType', 'value');
      const propertyValues = value && !isEmpty(value) ? [{ value }] : null;

      if (!propertyValues) {
        this.set('isError', true);
        this.set('errorMessage', 'investigateFiles.filter.invalidFilterInput');
      } else if (propertyValues[0].value.length > 256) {
        this.set('isError', true);
        this.set('errorMessage', 'investigateFiles.filter.invalidFilterInputLength');
      } else if (evaluateTextAgainstRegEx(propertyValues, filterType)) {
        this.set('isError', true);
        this.set('errorMessage', `investigateFiles.filter.${invalidError}`);
      } else {
        this.set('isError', false);
        this.send('updateFilter', { restrictionType, propertyName, propertyValues });
      }
    }
  }
});

export default connect(undefined, dispatchToActions)(TextFilter);

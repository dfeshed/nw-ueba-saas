import { connect } from 'ember-redux';
import Component from 'ember-component';
import layout from './template';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';
import FilterMixin from 'investigate-hosts/mixins/content-filter-mixins';
import set from 'ember-metal/set';


import {
  updateFilter
} from 'investigate-hosts/actions/data-creators/filter';


const dispatchToActions = {
  updateFilter
};

/**
 * Filter for array data type. Displays the list of values as list with checkbox.
 * On selecting the checkbox content will be filtered
 * @public
 */
const ListFilter = Component.extend(FilterMixin, {
  layout,

  eventBus: service(),

  classNames: ['list-filter'],

  /**
   * Restriction type for the list filter
   * @public
   */
  restrictionType: 'ALL',

  /**
   * Prepared checkbox option for display
   * @param options
   * @param selections
   * @returns {Array}
   * @public
   */
  @computed('config.options', 'config.selected')
  checkBoxOptions(options, selections) {
    return options.map((item) => {
      return { name: item, selected: selections.includes(item) };
    });
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
      const values = propertyValues.map((item) => item.value);
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
      this.set('selected', propertyValues.mapBy('value'));
    } else {
      this.set('selected', []);
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
      const propertyValues = values.length ? values : null;
      const {
        config: { propertyName },
        restrictionType,
        checkBoxOptions
      } = this.getProperties('restrictionType', 'propertyName', 'checkBoxOptions');

      const values = checkBoxOptions
        .filterBy('selected', true)

        .map((item) => {
          return { value: item.name };
        });

      set(option, 'selected', !selected);
      this.send('updateFilter', { propertyName, restrictionType, propertyValues });
    }
  }
});
export default connect(undefined, dispatchToActions)(ListFilter);

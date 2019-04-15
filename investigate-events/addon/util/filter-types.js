import EmberObject from '@ember/object';
import {
  COMPLEX_FILTER,
  QUERY_FILTER,
  TEXT_FILTER
} from 'investigate-events/constants/pill';

const { log } = console; // eslint-disable-line

/**
 * Base filter class.
 */
const Filter = EmberObject.extend({
  id: undefined,
  isFocused: false,
  isSelected: false,
  type: undefined
});

/**
 * Filters that can be modified and validated
 */
const InteractiveFilter = Filter.extend({
  isEditing: false,
  isInvalid: false,
  isValidationInProgress: false,
  validationError: undefined
});

/**
 * Query filter class.
 * @extends Filter
 */
const QueryFilter = InteractiveFilter.extend({
  componentName: 'query-container/query-pill',
  meta: undefined,
  operator: undefined,
  value: undefined,

  init() {
    this.set('type', QUERY_FILTER);
  }
});

/**
 * Query filter class.
 * @extends Filter
 */
const ComplexFilter = InteractiveFilter.extend({
  complexFilterText: undefined,
  componentName: 'query-container/complex-pill',

  init() {
    this.set('type', COMPLEX_FILTER);
  }
});

/**
 * Text filter class.
 */
const TextFilter = InteractiveFilter.extend({
  componentName: 'query-container/text-pill',
  searchTerm: undefined,

  init() {
    this.set('type', TEXT_FILTER);
  }
});

export {
  ComplexFilter,
  QueryFilter,
  TextFilter
};
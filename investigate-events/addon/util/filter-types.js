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
class Filter extends EmberObject {
  constructor() {
    super(...arguments);
  }

  init() {
    this.type = this.type || undefined;
  }
}

/**
 * Query filter class.
 * @extends Filter
 */
class QueryFilter extends Filter {
  constructor() {
    super({ type: QUERY_FILTER });
  }

  init() {
    this.meta = this.meta || undefined;
    this.operator = this.operator || undefined;
    this.value = this.value || undefined;
  }

  get componentName() {
    return 'query-container/query-pill';
  }

  /**
   * String representation of this filter with spaces trimmed.
   */
  toString() {
    const m = this.meta ? this.meta.trim() : '';
    const o = this.operator ? this.operator.trim() : '';
    const v = this.value ? this.value.trim() : '';
    return `${m} ${o} ${v}`.trim();
  }
}

/**
 * Query filter class.
 * @extends Filter
 */
class ComplexFilter extends Filter {
  constructor() {
    super({ type: COMPLEX_FILTER });
  }

  init() {
    this.complexFilterText = this.complexFilterText || undefined;
  }

  get componentName() {
    return 'query-container/complex-pill';
  }

  /**
   * String representation of this filter with spaces trimmed.
   */
  toString() {
    return this.complexFilterText ? this.complexFilterText.trim() : '';
  }
}

/**
 * Text filter class.
 */
class TextFilter extends Filter {
  constructor() {
    super({ type: TEXT_FILTER });
  }

  init() {
    this.searchTerm = this.searchTerm || undefined;
  }

  get componentName() {
    return 'query-container/text-pill';
  }

  /**
   * String representation of this filter with spaces trimmed.
   */
  toString() {
    return this.searchTerm ? this.searchTerm.trim() : '';
  }
}

export {
  ComplexFilter,
  QueryFilter,
  TextFilter
};
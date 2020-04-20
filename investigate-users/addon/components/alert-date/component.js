import { assign } from '@ember/polyfills';
import computed from 'ember-computed-decorators';
import DateFilter from 'rsa-data-filters/components/rsa-data-filters/filters/date-filter/component';

export default DateFilter.extend({

  @computed('filterOptions')
  options: {
    get() {
      const options = assign({}, this.get('defaults'), this.get('filterOptions'));
      const { filterValue: { value } } = options;
      this.set('hasCustomDate', value.compact().length === 2);
      return options;
    },

    set(key, value) {
      return value;
    }
  }
});

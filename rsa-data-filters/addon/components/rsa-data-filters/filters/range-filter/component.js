import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { assign } from '@ember/polyfills';

export default Component.extend({
  layout,

  classNames: ['range-filter'],

  defaults: {
    filterValue: {

    },
    max: 100,
    min: 0,
    pips: {
      mode: 'values',
      values: [],
      density: 4
    }
  },

  @computed('options')
  filterValue(options) {
    const { filterValue } = options;
    let { min: start, max: end } = options;
    if (filterValue && filterValue.length) {
      start = filterValue[0];
      end = filterValue[1];
    }
    return [start, end];
  },

  init() {
    this._super(arguments);
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    this.set('options', options);

  },

  actions: {
    onSliderChange(value) {
      const onChange = this.get('onChange');
      const name = this.get('options.name');
      if (onChange) {
        onChange({ name, operator: 'BETWEEN', value });
      }
    }
  }
});

import Component from '@ember/component';
import layout from './template';
import { assign } from '@ember/polyfills';
import computed from 'ember-computed-decorators';


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
      density: 10
    }
  },

  @computed('options')
  filterValue: {
    get() {
      const options = this.get('options');
      const { filterValue } = options;

      let { min: start, max: end } = options;
      if (filterValue && filterValue.length) {
        start = filterValue[0];
        end = filterValue[1];
      }
      return [start, end];
    },

    set(key, value) {
      return value;
    }

  },

  init() {
    this._super(arguments);
    const options = assign({}, this.get('defaults'), this.get('filterOptions'));
    this.set('options', options);
  },


  didReceiveAttrs() {
    this._super(...arguments);
    const isReset = this.get('isReset');
    if (isReset) {
      this.notifyPropertyChange('filterValue');
    }
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

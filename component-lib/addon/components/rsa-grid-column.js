import Ember from 'ember';
import layout from '../templates/components/rsa-grid-column';

const {
  Component,
  computed,
  computed: {
    equal
  }
} = Ember;

export default Component.extend({

  layout,

  classNames: ['rsa-grid-column'],

  classNameBindings: ['spanClass',
    'isNestedGrid',
    'isCollapsed',
    'centerChild',
    'isLow',
    'isMedium',
    'isHigh',
    'isDanger'],

  span: null,

  spanClass: computed('span', function() {
    return `rsa-grid-column-span-${this.get('span')}`;
  }),

  isScrollable: false,

  isCollapsed: false,

  isNestedGrid: false,

  centerChild: false,

  style: null, // ['low', 'medium', 'high', 'danger']

  isLow: equal('style', 'low'),
  isMedium: equal('style', 'medium'),
  isHigh: equal('style', 'high'),
  isDanger: equal('style', 'danger')

});
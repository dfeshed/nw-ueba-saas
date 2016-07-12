import Ember from 'ember';
import layout from '../templates/components/rsa-grid-column';

export default Ember.Component.extend({

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

  spanClass: Ember.computed('span', function() {
    return `rsa-grid-column-span-${this.get('span')}`;
  }),

  isScrollable: false,

  isCollapsed: false,

  isNestedGrid: false,

  centerChild: false,

  style: null, // ['low', 'medium', 'high', 'danger']

  isLow: Ember.computed.equal('style', 'low'),
  isMedium: Ember.computed.equal('style', 'medium'),
  isHigh: Ember.computed.equal('style', 'high'),
  isDanger: Ember.computed.equal('style', 'danger')

});
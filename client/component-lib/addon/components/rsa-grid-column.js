import Ember from 'ember';
import layout from '../templates/components/rsa-grid-column';

export default Ember.Component.extend({

  layout,

  classNames: ['rsa-grid-column'],

  classNameBindings: ['spanClass',
                      'isNestedGrid',
                      'isCollapsed',
                      'centerChild:center-child'],

  span: null,

  spanClass: (function() {
    return `rsa-grid-column-span-${this.get('span')}`;
  }).property('span'),

  isScrollable: false,

  isCollapsed: false,

  isNestedGrid: false,

  centerChild: false

});

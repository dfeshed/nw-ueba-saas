import Ember from 'ember';

export default Ember.Component.extend({

  tagName: 'i',

  classNames: ['rsa-icon'],

  attributeBindings: ['title'],

  classNameBindings: ['isLined:is-lined:is-filled',
                      'iconClass',
                      'isSmaller',
                      'isSmall',
                      'isLarge',
                      'isLarger',
                      'isLargest'],

  name: null,

  style: 'filled', // ['filled', 'lined']

  size: null, // ['smaller', 'small', 'large', 'larger', 'largest']

  isLined: Ember.computed.equal('style', 'lined'),

  isSmaller: Ember.computed.equal('size', 'smaller'),

  isSmall: Ember.computed.equal('size', 'small'),

  isLarge: Ember.computed.equal('size', 'large'),

  isLarger: Ember.computed.equal('size', 'larger'),

  isLargest: Ember.computed.equal('size', 'largest'),

  iconClass: Ember.computed('name', function() {
    return `rsa-icon-${this.get('name')}`;
  })

});
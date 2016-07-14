import Ember from 'ember';

const {
  Component,
  computed,
  computed: {
    equal
  }
} = Ember;

export default Component.extend({

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

  isLined: equal('style', 'lined'),

  isSmaller: equal('size', 'smaller'),

  isSmall: equal('size', 'small'),

  isLarge: equal('size', 'large'),

  isLarger: equal('size', 'larger'),

  isLargest: equal('size', 'largest'),

  iconClass: computed('name', function() {
    return `rsa-icon-${this.get('name')}`;
  })

});
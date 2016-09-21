import Ember from 'ember';
import computed, { equal } from 'ember-computed-decorators';

const { Component } = Ember;

export default Component.extend({

  tagName: 'i',

  classNames: ['rsa-icon'],

  attributeBindings: ['title'],

  classNameBindings: [
    'active',
    'isLined:is-lined:is-filled',
    'iconClass',
    'isSmaller',
    'isSmall',
    'isLarge',
    'isLarger',
    'isLargest'
  ],

  active: false,
  name: null,
  size: null, // ['smaller', 'small', 'large', 'larger', 'largest']
  style: 'filled', // ['filled', 'lined']

  @equal('style', 'lined') isLined: null,
  @equal('size', 'small') isSmall: null,
  @equal('size', 'smaller') isSmaller: null,
  @equal('size', 'large') isLarge: null,
  @equal('size', 'larger') isLarger: null,
  @equal('size', 'largest') isLargest: null,

  @computed('name')
  iconClass(name) {
    return `rsa-icon-${name}`;
  }
});

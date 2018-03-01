import Component from '@ember/component';
import computed, { equal } from 'ember-computed-decorators';

export default Component.extend({
  tagName: 'i',

  classNames: ['rsa-icon'],

  attributeBindings: ['title', 'testId:test-id'],

  classNameBindings: [
    'active',
    'disabled',
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

  @computed('name', 'style')
  iconClass(name, style) {
    return `rsa-icon-${name}-${style}`;
  }
});

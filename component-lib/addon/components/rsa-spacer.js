import Ember from 'ember';
import layout from '../templates/components/rsa-spacer';
import CspStyleMixin from 'ember-cli-csp-style/mixins/csp-style';

const { Component } = Ember;

export default Component.extend(CspStyleMixin, {
  layout,
  tagName: 'div',
  classNames: 'rsa-spacer',
  width: 0,
  height: 0,
  styleBindings: ['width[px]', 'height[px]']
});

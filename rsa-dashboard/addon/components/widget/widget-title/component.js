import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  classNameBindings: ['style'],

  attributeBindings: ['testId:test-id'],

  style: 'widget-title',

  testId: 'widget-title'

});

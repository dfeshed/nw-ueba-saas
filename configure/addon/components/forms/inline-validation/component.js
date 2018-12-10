import Component from '@ember/component';

export default Component.extend({
  tagName: 'ul',
  testId: 'formGroupValidation',
  attributeBindings: ['testId:test-id']
});

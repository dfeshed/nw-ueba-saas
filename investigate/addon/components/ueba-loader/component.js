import Component from '@ember/component';

export default Component.extend({
  testId: 'uebaLoader',
  attributeBindings: ['testId:test-id'],
  classNames: ['ueba-loading__main']
});

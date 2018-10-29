import layout from './template';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  tagName: 'dl',
  testId: 'propertiesFor',
  attributeBindings: ['testId:test-id'],
  classNames: ['properties-for'],

  @computed()
  member() {
    return {
      type: 'array',
      name: 'related_links',
      isNestedValue: true
    };
  }
});

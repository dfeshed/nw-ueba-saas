import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { guidFor } from '@ember/object/internals';

export default Component.extend({
  testId: 'formGroup',
  attributeBindings: ['testId:test-id'],
  labelHidden: false,
  classNames: ['form-group'],

  @computed()
  guid() {
    return guidFor(this);
  },

  @computed('guid')
  inputId(guid) {
    return `${guid}-input`;
  },

  @computed('guid')
  descriptionId(guid) {
    return `${guid}-description`;
  }
});

import { set, get } from '@ember/object';
import Component from '@ember/component';

export default Component.extend({
  tagName: 'input',
  testId: 'formGroupInput',
  _legitValue: undefined,
  attributeBindings: [
    'min',
    'max',
    'type',
    'testId:test-id',
    'value',
    'ariaDescribedBy:aria-describedby'
  ],
  input(event) {
    const value = this.readDOMAttr('value');
    const legitValue = get(this, '_legitValue');
    if (legitValue !== value || (legitValue === '' && value === '')) {
      set(this, '_legitValue', value);
      get(this, 'update')(value, event);
    }
  }
});

import { computed } from '@ember/object';
import layout from './template';
import { isNone } from '@ember/utils';
import Component from '@ember/component';

const trim = (value) => value.toString().replace(/\s\s+/g, ' ').trim();

export default Component.extend({
  layout,
  tagName: '',

  hasValue: computed('value', function() {
    const value = this.value && trim(this.value);
    return value && !isNone(value);
  }),

  childTagName: computed('tag', function() {
    return this.tag || 'dd';
  }),

  childTestId: computed('testId', function() {
    return this.testId || 'keyValue';
  })
});

import layout from './template';
import { isNone } from '@ember/utils';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';

const trim = (value) => value.toString().replace(/\s\s+/g, ' ').trim();

const NaValue = Component.extend({
  layout,
  tagName: '',
  @computed('value')
  hasValue(incoming) {
    const value = incoming && trim(incoming);
    return value && !isNone(value);
  }
});

NaValue.reopenClass({
  positionalParams: ['value']
});

export default NaValue;

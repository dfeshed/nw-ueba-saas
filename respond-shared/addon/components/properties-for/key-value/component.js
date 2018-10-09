import layout from './template';
import Component from '@ember/component';
import { get } from '@ember/object';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { underscore, capitalize } from '@ember/string';

function missingTranslation(value) {
  return !value || (value.toString().indexOf('Missing translation') > -1);
}

function capitalizeResult(value) {
  return underscore(value).split('_').map(capitalize).join(' ');
}

export default Component.extend({
  layout,
  tagName: 'tr',
  testId: 'keyValueRow',
  classNames: ['key'],
  attributeBindings: ['testId:test-id'],
  i18n: service(),

  @computed('member.name', 'itemPath', 'i18n.locale')
  formattedName(name, itemPath) {
    if (!name) {
      return '';
    }
    const i18n = get(this, 'i18n');
    const prefix = 'respond.eventDetails.labels.';
    const postfix = itemPath.replace(/\./g, '_');
    const result = i18n.t(`${prefix}${postfix}`, { default: `${prefix}${name}` });
    return missingTranslation(result) ? capitalizeResult(name) : result;
  }
});

import layout from './template';
import Component from '@ember/component';
import { get, computed } from '@ember/object';
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
  testId: 'keyValueRow',
  classNames: ['key-wrapper'],
  attributeBindings: ['testId:test-id'],
  nestedComponentClass: 'properties-for',
  i18n: service(),

  formattedName: computed('member.name', 'itemPath', 'i18n.locale', function() {
    if (!this.member?.name) {
      return '';
    }
    const i18n = get(this, 'i18n');
    const prefix = 'respond.eventDetails.labels.';
    const postfix = this.itemPath ? this.itemPath.replace(/\./g, '_') : '';
    const result = i18n.t(`${prefix}${postfix}`, { default: `${prefix}${this.member?.name}` });
    return missingTranslation(result) ? capitalizeResult(this.member?.name) : result;
  })
});

import Component from 'ember-component';
import computed, { notEmpty } from 'ember-computed-decorators';
import { dasherize } from 'ember-string';

const EntitiesLegend = Component.extend({
  classNames: ['rsa-incident-entities-legend'],
  classNameBindings: ['hasData:has-data:has-no-data'],
  data: null,
  selection: null,

  @notEmpty('data')
  hasData: null,

  @computed('selection')
  resolvedSelection(selection) {
    if (!selection || !selection.ids.length) {
      return null;
    } else if (String(selection.type).match(/node|link/)) {
      return null;
    } else {
      return selection;
    }
  },

  // Same key-value pairs as in `data`, but with strings added for UI display.
  // @private
  @computed('data')
  resolvedData(data) {
    return (data || []).map(({ key, value }) => ({
      key,
      cssClass: dasherize(String(key)),
      i18nKey: `respond.entity.legend.${key}`,
      value
    }));
  }
});

export default EntitiesLegend;

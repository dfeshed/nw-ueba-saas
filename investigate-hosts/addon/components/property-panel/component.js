import computed from 'ember-computed-decorators';
import { assert } from '@ember/debug';
import config from './overview-property-config';
import PropertyPanel from 'investigate-shared/components/endpoint/base-property-panel/component';
import { get } from '@ember/object';
import _ from 'lodash';
import { inject as service } from '@ember/service';

/**
 * Overide the the `endpoint/base-property-panel` to accommodate different json structure
 * @public
 */
export default PropertyPanel.extend({

  config,

  features: service(),

  @computed('data', 'config')
  properties(data, config) {
    assert('Cannot instantiate Summary panel without configuration.', config);
    config = this.updateConfig(data, config);
    if (data) {
      const i18n = this.get('i18n');
      // Loop through the list of property and set the value for each field
      const properties = config.map((item) => {
        // Loop through all the fields and set the value and display name
        const fields = item.fields.map((fieldItem) => {
          const { fieldPrefix, field, labelKey, isStandardString } = fieldItem;
          const label = isStandardString ? labelKey : `${this.get('localeNameSpace')}.${labelKey}`;
          // If field prefix combine that field to get the value
          let value = null;
          if (isStandardString) {
            value = field;
          } else {
            value = (fieldPrefix ? get(data, `${fieldPrefix}.${field}`) : get(data, field));
          }
          if (value && field === 'groupPolicy.groups') {
            value = value.map((v) => {
              return v.name;
            });
          }
          return {
            ...fieldItem,
            value,
            displayName: isStandardString ? label : i18n.t(label).string
          };
        });
        return { ...item, fields };
      });
      return properties;
    }
    return [];
  },
  /**
   * Host has array of property values, currently common property panel does not support array. Need to override the
   * property panel to handle this. That multioption set in the config
   *
   * Ex : networkInterfaces: [
   *  { object },
   *  { object }
   * ]
   * Need to be displayed as
   * Network Interface:
   * ----
   *
   * Network Interface:
   * ----
   *
   * @override
   * @method properties
   * @param data
   * @private
   * @returns {Array}
   */

  updateConfig(data, config) {
    let newConfigList = [];
    if (data) {
      newConfigList = config.map((item) => {
        const { multiOption, prefix } = item;
        if (multiOption) {
          const values = get(data, prefix) || [];
          const multiOptionConfigList = values.map((value, index) => ({
            ...item,
            fields: item.fields.map((fieldItem) => ({
              ...fieldItem,
              fieldPrefix: `${item.prefix}.${index}`
            }))
          }));
          return multiOptionConfigList;
        } else {
          return item;
        }
      });
    }
    return _.flatten(newConfigList);
  }
});

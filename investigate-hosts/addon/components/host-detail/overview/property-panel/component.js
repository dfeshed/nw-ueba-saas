import computed from 'ember-computed-decorators';
import { assert } from '@ember/debug';
import config from './overview-property-config';
import PropertyPanel from 'investigate-hosts/components/host-detail/base-property-panel/component';
import { get } from '@ember/object';
import _ from 'lodash';

/**
 * Overide the the `host-detail/base-property-panel` to accommodate different json structure
 * @public
 */
export default PropertyPanel.extend({

  config,

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
          const { fieldPrefix, field, labelKey } = fieldItem;
          const label = `${this.get('localeNameSpace')}.${labelKey}`;
          // If field prefix combine that field to get the value
          const value = fieldPrefix ? get(data, `${fieldPrefix}.${field}`) : get(data, field);
          return {
            ...fieldItem,
            value,
            displayName: i18n.t(label).string
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

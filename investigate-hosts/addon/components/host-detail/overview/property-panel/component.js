import computed from 'ember-computed-decorators';
import { assert } from 'ember-metal/utils';
import config from './overview-property-config';
import PropertyPanel from 'investigate-hosts/components/host-detail/base-property-panel/component';
import set from 'ember-metal/set';
import get from 'ember-metal/get';

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
          set(fieldItem, 'value', value);
          set(fieldItem, 'displayName', i18n.t(label));
          return fieldItem;
        });
        set(item, 'fields', fields);
        return item;
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
    let newConfig = [];

    if (data) {
      newConfig = config.map((item) => {
        const { multiOption, prefix } = item;
        if (multiOption) {
          const values = get(data, prefix);
          if (values) {
            for (let i = 0; i < values.length; i++) {
              item.fields = item.fields.map((fieldItem) => {
                return {
                  field: fieldItem.field,
                  labelKey: fieldItem.labelKey,
                  fieldPrefix: `${item.prefix}.${i}`
                };
              });
              if (i === (values.length - 1)) {
                return item;
              }
              newConfig.push(item);
            }
          } else {
            return item;
          }
        } else {
          return item;
        }
      });
    }
    return newConfig;
  }
});

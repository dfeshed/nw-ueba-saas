import classic from 'ember-classic-decorator';
import { inject as service } from '@ember/service';
import { assert } from '@ember/debug';
import config from './overview-property-config';
import PropertyPanel from 'investigate-shared/components/endpoint/base-property-panel/component';
import { get, computed } from '@ember/object';
import _ from 'lodash';

/**
 * Overide the the `endpoint/base-property-panel` to accommodate different json structure
 * @public
 */
@classic
export default class _PropertyPanel extends PropertyPanel {
  config = config;

  @service
  features;

  @computed('data', 'config')
  get properties() {
    assert('Cannot instantiate Summary panel without configuration.', this.config);
    const updatedConfig = this.updateConfig(this.data, this.config);
    if (this.data) {
      const i18n = this.get('i18n');
      // Loop through the list of property and set the value for each field
      const properties = updatedConfig.map((item) => {
        // Loop through all the fields and set the value and display name
        const fields = item.fields.map((fieldItem) => {
          const { fieldPrefix, field, labelKey, isStandardString } = fieldItem;
          const label = isStandardString ? labelKey : `${this.get('localeNameSpace')}.${labelKey}`;
          // If field prefix combine that field to get the value
          let value = null;
          if (isStandardString) {
            value = field;
          } else {
            value = (fieldPrefix ? get(this.data, `${fieldPrefix}.${field}`) : get(this.data, field));
          }
          if (value && field === 'groupPolicy.groups') {
            value = value.map((v) => {
              return v.name;
            });
          }
          return {
            ...fieldItem,
            value,
            displayName: isStandardString ? label : i18n.t(label)
          };
        });
        return { ...item, fields };
      });
      return properties;
    }
    return [];
  }

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
}

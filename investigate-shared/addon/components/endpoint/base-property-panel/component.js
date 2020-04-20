import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { assert } from '@ember/debug';
import { isEmpty } from '@ember/utils';
import { set, get } from '@ember/object';
import { inject as service } from '@ember/service';

/**
 * Property panel to to display the key and value within given section
 * @public
 */
export default Component.extend({
  layout,

  tagName: 'vbox',

  classNames: ['host-property-panel'],

  i18n: service(),

  /**
   * Namespace for the locale.
   * @type {string}
   * @default: 'investigateHosts.property'
   * @requires localeNameSpace
   * @public
   */
  localeNameSpace: 'investigateHosts.property',

  /**
   * Title of the property panel
   * @type {string}
   * @default 'Property Panel'
   * @requires title
   * @public
   */
  title: '',

  /**
   * Bind value to the search box.
   * @private
   */
  _searchText: '',

  /**
   * By default show only nonEmptyProperty
   * @public
   */
  showNonEmptyProperty: true,

  /**
   * Data to display
   * @type {object}
   * @requires data
   * @public
   */
  data: undefined,

  /**
   * Required configuration that specifies properties to be displayed.
   * The following formats for `config` is supported:
   * An array of objects. Each object can be either a POJO or an Ember.Object. Each object represents a section
   * to be displayed with the following properties:
   * (i) `sectionName`: The name of section under which all the properties are displayed. Required.
   * (ii)`fieldPrefix` : Prefix for field, this value is prepended to each `field` property inside `fields`
   * (iii) `fields`: Array of objects, Each object can be either a POJO or an Ember.Object. Each object represents a
   *  property to to displayed inside the section, with following properties:
   *    `field`: The name of the JSON field from which to read the display value. Required
   *    `format`: Format used the display the value. If format is not specified values are displayed as is. Optional
   *    `labelKey`: Locale string for the property to be displayed
   *
   * @type {object[]}
   * @public
   */
  config: undefined,

  errorMessage: null,
  /**
   * Override this method if changes required in config before processing.
   * @param config
   * @returns {*}
   * @private
   */
  updateConfig(data, config) {
    return config;
  },

  /**
   * Prepares the property array setting the value to the each property reading it from the data.
   * @param data
   * @returns {Array}
   * @public
   */
  @computed('data', 'config')
  properties(data, config) {
    assert('Cannot instantiate property panel without configuration.', config);
    if (data) {
      const i18n = this.get('i18n');
      const clonedConfig = [...config];
      this.updateConfig(data, clonedConfig);
      // Loop through the list of property and set the value for each field
      const properties = clonedConfig.map((item) => {
        let { fieldPrefix } = item;
        // Loop through all the fields and set the value and display name
        const fields = item.fields.map((fieldItem) => {
          const { field } = fieldItem;
          const labelKey = fieldItem.labelKey || field;
          fieldPrefix = fieldItem.fieldPrefix || fieldPrefix;
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
   * Filter the properties based on the search text and `showNonEmptyProperty`
   * @public
   * @param searchText
   * @param properties
   * @param showNonEmptyProperty
   */
  @computed('_searchText', 'properties', 'showNonEmptyProperty')
  visibleProperty(searchText, properties, showNonEmptyProperty) {
    const props = properties.map((prop, index) => {
      // Add css class to this section so we can hide unwanted fields
      if (index === 1) {
        prop = prop.fields[0].value === 'Manual' ? { ...prop, sectionClass: 'manual-section' } : prop;
      }
      // Filter the fields based on searchText
      const fields = prop.fields.filter((field) => {
        const flag = showNonEmptyProperty ? (!isEmpty(field.value) || field.field === 'signature.features') : true;
        const displayName = field.displayName || '';
        const value = field.value || '';
        const valueFlag = value.toString().toLowerCase().includes(searchText.toLowerCase());
        const propertyFlag = displayName.toLowerCase().includes(searchText.toLowerCase());
        return flag && (propertyFlag || valueFlag);
      });
      return { ...prop, fields };
    });
    return props.filter((item) => item.fields.length); // Don' include the section if empty fields
  },

  init() {
    this._super(...arguments);
    this.config = this.config || [];
    this.data = this.data || {};
  },

  actions: {
    toggleIsIncludeEmptyValue() {
      this.toggleProperty('showNonEmptyProperty');
    }
  }
});

import classic from 'ember-classic-decorator';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import Component from '@ember/component';
import layout from './template';
import { set, get, computed } from '@ember/object';

@classic
@templateLayout(layout)
@classNames('process-property-panel')
export default class ProcessPropertyPanel extends Component {
  /**
   * Namespace for the locale.
   * @type {string}
   * @default: 'investigateProcessAnalysis.property.file'
   * @requires localeNameSpace
   * @public
   */
  localeNameSpace = 'investigateProcessAnalysis.property.file';

  /**
   * Title of the property panel
   * @type {string}
   * @default 'Property Panel'
   * @requires title
   * @public
   */
  title = 'Property Panel';

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

  init() {
    super.init(...arguments);
    this.config = this.config || [];
  }

  /**
   * Data to display
   * @type {object}
   * @requires data
   * @public
   */
  data = null;

  @computed('data', 'config')
  get properties() {
    if (this.data && this.config) {
      const i18n = this.get('i18n');
      const clonedConfig = [...this.config];
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
          const value = fieldPrefix ? get(this.data, `${fieldPrefix}.${field}`) : get(this.data, field);
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
  }
}

import Component from '@ember/component';
import layout from './template';
import DEFAULT_PROPERTY_CONFIG from './file-analysis-base-property-config';
import FILE_FORMAT_SPECIFIC_PROPERTY_CONFIG from './format-specific-config';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  tagName: 'box',

  classNames: ['file-analysis-properties'],

  @computed('filePropertiesData')
  filePropertyConfig(filePropertiesData) {
    const { format } = filePropertiesData;
    const formatSpecificConfig = FILE_FORMAT_SPECIFIC_PROPERTY_CONFIG[format] ? FILE_FORMAT_SPECIFIC_PROPERTY_CONFIG[format] : FILE_FORMAT_SPECIFIC_PROPERTY_CONFIG.default;
    return [...DEFAULT_PROPERTY_CONFIG, ...formatSpecificConfig];
  }

});
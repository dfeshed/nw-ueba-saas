import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { assert } from '@ember/debug';
import _ from 'lodash';

/**
 * Component for displaying the overview items
 * @public
 */
export default Component.extend({

  classNames: ['header-data'],

  propertyComponent: 'host-detail/process/summary-items/property',

  config: null,

  data: null,

  /**
   * Update the configuration with data
   * @param data
   * @returns {Array}
   * @public
   */
  @computed('data', 'config')
  summaryData(data, config) {
    assert('Cannot instantiate Summary panel without configuration.', config);
    if (data) {
      const items = config.map((item) => {
        const value = _.get(data, item.field) || 'N/A';
        return { ...item, value };
      });
      return items;
    }
    return [];
  }
});

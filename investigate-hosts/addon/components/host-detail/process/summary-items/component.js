import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { assert } from '@ember/debug';
import _ from 'lodash';


const stateToComputed = (state) => ({
  sid: state.endpointQuery.serverId
});
/**
 * Component for displaying the Process Summary items
 * @public
 */
const SummaryItemsComponent = Component.extend({

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
        const value = _.get(data, item.field) || '--';
        const checksum = _.get(data, 'fileProperties.checksumSha256') || null;
        return { ...item, value, checksum };
      });
      return items;
    }
    return [];
  }
});
export default connect(stateToComputed)(SummaryItemsComponent);

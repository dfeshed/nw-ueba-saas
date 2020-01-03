import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { assert } from '@ember/debug';
import _ from 'lodash';


const stateToComputed = (state) => ({
  sid: state.endpointQuery.selectedMachineServerId
});

/**
 * Component for displaying the Process Summary items
 * @public
 */
@classic
@classNames('header-data')
class SummaryItemsComponent extends Component {
  propertyComponent = 'host-detail/process/summary-items/property';
  config = null;
  data = null;

  @computed('data', 'config')
  get summaryData() {
    assert('Cannot instantiate Summary panel without configuration.', this.config);
    if (this.data) {
      const items = this.config.map((item) => {
        const value = _.get(this.data, item.field) || '--';
        const checksum = _.get(this.data, 'fileProperties.checksumSha256') || null;
        const sourceSid = _.get(this.data, 'fileProperties.downloadInfo.serviceId') || null;
        return { ...item, value, checksum, sourceSid };
      });
      return items;
    }
    return [];
  }
}

export default connect(stateToComputed)(SummaryItemsComponent);

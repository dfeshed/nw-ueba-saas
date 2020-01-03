import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedTabComponent } from 'investigate-hosts/reducers/visuals/selectors';
import { isSnapshotsAvailable } from 'investigate-hosts/reducers/details/overview/selectors';
import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';
import { downloadLink } from 'investigate-hosts/reducers/details/selectors';

const stateToComputed = (state) => ({
  selectedTabComponent: selectedTabComponent(state),
  isSnapshotsAvailable: isSnapshotsAvailable(state),
  isSnapshotsLoading: state.endpoint.detailsInput.isSnapshotsLoading,
  selectedServiceData: selectedServiceWithStatus(state),
  downloadLink: downloadLink(state)
});

@classic
@tagName('')
class DetailComponent extends Component {
  @computed('selectedTabComponent', 'isSnapshotsAvailable')
  get showSnapshotEmptyMessage() {
    if (this.isSnapshotsAvailable) {
      return false;
    } else {
      // if snapshots are not there show empty message for all except (overview and system tab)
      return !['host-detail/overview', 'host-detail/system-information', 'host-detail/downloads'].includes(this.selectedTabComponent);
    }
  }
}

export default connect(stateToComputed)(DetailComponent);

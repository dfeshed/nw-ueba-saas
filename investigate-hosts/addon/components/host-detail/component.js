import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedTabComponent } from 'investigate-hosts/reducers/visuals/selectors';
import computed from 'ember-computed-decorators';
import { isSnapshotsAvailable } from 'investigate-hosts/reducers/details/overview/selectors';

const stateToComputed = (state) => ({
  selectedTabComponent: selectedTabComponent(state),
  isSnapshotsAvailable: isSnapshotsAvailable(state),
  hostDetailsLoading: state.endpoint.visuals.hostDetailsLoading
});

const DetailComponent = Component.extend({

  tagName: '',

  @computed('selectedTabComponent', 'isSnapshotsAvailable')
  showSnapshotEmptyMessage(selectedTabComponent, isSnapshotsAvailable) {
    if (isSnapshotsAvailable) {
      return false;
    } else {
      // if snapshots are not there show empty message for all except (overview and system tab)
      return !['host-detail/overview', 'host-detail/system-information'].includes(selectedTabComponent);
    }
  }

});
export default connect(stateToComputed)(DetailComponent);

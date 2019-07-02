import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedTabComponent } from 'investigate-hosts/reducers/visuals/selectors';
import computed from 'ember-computed-decorators';
import { isSnapshotsAvailable } from 'investigate-hosts/reducers/details/overview/selectors';
import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';
import { downloadLink } from 'investigate-hosts/reducers/details/selectors';

const stateToComputed = (state) => ({
  selectedTabComponent: selectedTabComponent(state),
  isSnapshotsAvailable: isSnapshotsAvailable(state),
  isSnapshotsLoading: state.endpoint.detailsInput.isSnapshotsLoading,
  isProcessDetailsView: state.endpoint.visuals.isProcessDetailsView,
  selectedServiceData: selectedServiceWithStatus(state),
  isFileAnalysisView: state.endpoint.fileAnalysis.isFileAnalysisView,
  downloadLink: downloadLink(state),
  isMFTView: state.endpoint.hostDownloads.downloads.isShowMFTView
});

const DetailComponent = Component.extend({

  tagName: '',

  @computed('selectedTabComponent', 'isSnapshotsAvailable')
  showSnapshotEmptyMessage(selectedTabComponent, isSnapshotsAvailable) {
    if (isSnapshotsAvailable) {
      return false;
    } else {
      // if snapshots are not there show empty message for all except (overview and system tab)
      return !['host-detail/overview', 'host-detail/system-information', 'host-detail/downloads'].includes(selectedTabComponent);
    }
  }

});
export default connect(stateToComputed)(DetailComponent);

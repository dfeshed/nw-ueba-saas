import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  selectedMftFile: state.endpoint.hostDownloads.downloads.selectedMftFile,
  focusedHost: state.endpoint.detailsInput.agentId,
  isMFTView: state.endpoint.hostDownloads.downloads.isShowMFTView
});

const BodyCellComponent = BodyCell.extend({

  dateFormat: service(),

  timeFormat: service(),

  timezone: service(),

  @computed('item')
  downloadInfo(item) {
    const { status, error } = item;
    return { status, error };
  }


});
export default connect(stateToComputed, undefined)(BodyCellComponent);
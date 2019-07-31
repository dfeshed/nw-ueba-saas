import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { setSelectDirectoryForDetails, getSubDirectories, setSelectedParentDirectory } from 'investigate-hosts/actions/data-creators/downloads';
import { next } from '@ember/runloop';


const stateToComputed = (state) => ({
  selectedMftFile: state.endpoint.hostDownloads.downloads.selectedMftFile,
  focusedHost: state.endpoint.detailsInput.agentId,
  isMFTView: state.endpoint.hostDownloads.downloads.isShowMFTView
});

const dispatchToActions = {
  setSelectDirectoryForDetails,
  getSubDirectories,
  setSelectedParentDirectory
};

const BodyCellComponent = BodyCell.extend({

  dateFormat: service(),

  timeFormat: service(),

  timezone: service(),

  @computed('item')
  downloadInfo(item) {
    const { status, error } = item;
    return { status, error };
  },

  actions: {
    onFetchSubdirectories(data) {
      const { recordNumber, ancestors, parentDirectory } = data;

      const parentAncestors = ancestors.asMutable();
      parentAncestors.filter((item) => item !== parentDirectory);

      this.send('setSelectDirectoryForDetails', {
        selectedDirectoryForDetails: recordNumber,
        fileSource: 'drive',
        pageSize: 100,
        isDirectories: false,
        inUse: true });
      this.send('getSubDirectories');
      next(() => {
        this.send('setSelectedParentDirectory', {
          selectedParentDirectory: { recordNumber: parentDirectory, ancestors: parentAncestors, close: false },
          pageSize: 65000,
          isDirectories: true,
          inUse: true });
        this.send('getSubDirectories');
      });
    }

  }
});
export default connect(stateToComputed, dispatchToActions)(BodyCellComponent);
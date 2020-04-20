import classic from 'ember-classic-decorator';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';
import { connect } from 'ember-redux';
import { setSelectDirectoryForDetails, getSubDirectories, setSelectedParentDirectory } from 'investigate-hosts/actions/data-creators/downloads';
import { next } from '@ember/runloop';


const stateToComputed = (state) => ({
  selectedMftFile: state.endpoint.hostDownloads.downloads.selectedMftFile,
  focusedHost: state.endpoint.detailsInput.agentId
});

const dispatchToActions = {
  setSelectDirectoryForDetails,
  getSubDirectories,
  setSelectedParentDirectory
};

@classic
class BodyCellComponent extends BodyCell {
  @service
  dateFormat;

  @service
  timeFormat;

  @service
  timezone;

  @computed('item')
  get downloadInfo() {
    const { status, error } = this.item;
    return { status, error };
  }

  @action
  onFetchSubdirectories(data) {
    const { recordNumber, ancestors, parentDirectory, fullPathName, name } = data;

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
        inUse: true,
        fullPathName,
        name });
      this.send('getSubDirectories');
    });
  }
}

export default connect(stateToComputed, dispatchToActions)(BodyCellComponent);

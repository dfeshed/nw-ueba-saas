import classic from 'ember-classic-decorator';
import { computed } from '@ember/object';
import { inject as service } from '@ember/service';
import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';
import { connect } from 'ember-redux';

const stateToComputed = (state) => ({
  selectedMftFile: state.endpoint.hostDownloads.downloads.selectedMftFile,
  focusedHost: state.endpoint.detailsInput.agentId
});

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
    const { status, error, fileType } = this.item;
    return { status, error, fileType };
  }
}

export default connect(stateToComputed, undefined)(BodyCellComponent);

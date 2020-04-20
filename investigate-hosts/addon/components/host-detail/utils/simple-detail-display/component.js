import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { failure } from 'investigate-shared/utils/flash-messages';

@classic
@classNames('simple-detail-display-wrapper')
@tagName('hbox')
export default class SimpleDetailDisplay extends Component {
  selections = null;
  showFileStatusModel = false;

  @service
  accessControl;

  rowItem = null;

  isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('checksumSha256', item.checksumSha256);
    }
    return selected;
  }

  /*
   * A hash of inputs coming from consumers of this component.
   *
   * Possible values:
   *   status
   *   tableItems
   *   columnsConfig
   *   selectRowAction
   *   localeNameSpace
   *   propertyConfig
   *   propertyData
   */
  detailDisplayInputs = null;

  @computed('detailDisplayInputs')
  get datatableWidth() {
    return (this.detailDisplayInputs && this.detailDisplayInputs.propertyConfig) ? 'col-xs-9' : 'col-xs-12';
  }

  @action
  beforeContextMenuShow({ contextSelection: item }) {
    if (!this.isAlreadySelected(this.get('detailDisplayInputs.selectedFiles'), item)) {
      this.detailDisplayInputs.toggleOneItemSelectionAction(item);
    }
    const selections = this.get('selections');
    if (selections && selections.length === 1) {
      this.detailDisplayInputs.getSavedFileStatus();
    }
  }

  @action
  showEditFileStatus(item) {
    if (this.get('accessControl.endpointCanManageFiles')) {
      this.set('rowItem', item);
      this.set('showFileStatusModal', true);
    } else {
      failure('investigateFiles.noManagePermissions');
    }
  }

  @action
  onCloseEditFileStatus() {
    this.set('showFileStatusModal', false);
  }

  @action
  resetRiskScoreAction() {
    // Placeholder for the next PR.
  }
}
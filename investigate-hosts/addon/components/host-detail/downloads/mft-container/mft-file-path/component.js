import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { mftFilterVisible } from 'investigate-hosts/actions/data-creators/downloads';


const stateToComputed = (state) => ({
  selectedFileCount: state.endpoint.hostDownloads.downloads.selectedFileList.length,
  fullPathName: state.endpoint.hostDownloads.mft.mftDirectory.fullPathName,
  directoryName: state.endpoint.hostDownloads.mft.mftDirectory.name,
  expressionList: state.endpoint.hostDownloads.mft.filter.expressionList
});

const dispatchToActions = {
  mftFilterVisible
};

@classic
@tagName('section')
@classNames('mft-file-path')
class MftFilePath extends Component {
  @computed('fullPathName', 'directoryName')
  get location() {
    return this.directoryName ? `${this.fullPathName}${this.directoryName}` : '/';
  }

  @computed('expressionList')
  get filterCountLabel() {
    const NO_FILTERS = this.get('i18n').t('investigateHosts.downloads.mftViewer.no');
    return this.expressionList.length ? this.expressionList.length : NO_FILTERS;
  }

  @action
  filterAction() {
    this.send('mftFilterVisible', true);
    this.openFilterPanel();
  }
}

export default connect(stateToComputed, dispatchToActions)(MftFilePath);
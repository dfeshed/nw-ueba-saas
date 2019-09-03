import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
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
const MftFilePath = Component.extend({
  tagName: 'section',
  classNames: ['mft-file-path'],
  @computed('fullPathName', 'directoryName')
  location(fullPathName, directoryName) {
    return directoryName ? `${fullPathName}${directoryName}` : '/';
  },
  @computed('expressionList')
  filterCountLabel(expressionList) {
    const NO_FILTERS = this.get('i18n').t('investigateHosts.downloads.mftViewer.no');
    return expressionList.length ? expressionList.length : NO_FILTERS;
  },
  actions: {
    filterAction() {
      this.send('mftFilterVisible', true);
      this.openFilterPanel();
    }

  }
});
export default connect(stateToComputed, dispatchToActions)(MftFilePath);
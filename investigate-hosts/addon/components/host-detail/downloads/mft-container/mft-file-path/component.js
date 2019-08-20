import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';


const stateToComputed = (state) => ({
  selectedFileCount: state.endpoint.hostDownloads.downloads.selectedFileList.length,
  fullPathName: state.endpoint.hostDownloads.mft.mftDirectory.fullPathName,
  expressionList: state.endpoint.hostDownloads.mft.filter.expressionList
});

const MftFilePath = Component.extend({
  tagName: 'section',
  classNames: ['mft-file-path'],
  @computed('expressionList')
  filterCountLabel(expressionList) {
    const NO_FILTERS = this.get('i18n').t('investigateHosts.downloads.mftViewer.no');
    return expressionList.length ? expressionList.length : NO_FILTERS;
  }
});
export default connect(stateToComputed)(MftFilePath);
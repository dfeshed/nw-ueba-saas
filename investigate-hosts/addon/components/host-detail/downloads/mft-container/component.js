import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';


const stateToComputed = (state) => ({
  selectedMftName: state.endpoint.hostDownloads.downloads.selectedMftName,
  isShowMFTView: state.endpoint.hostDownloads.downloads.isShowMFTView
});
const mftContainer = Component.extend({
  tagName: 'box',
  classNames: ['mft-container'],
  accessControl: service()
});

export default connect(stateToComputed)(mftContainer);
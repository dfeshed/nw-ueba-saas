import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';


const stateToComputed = (state) => ({
  selectedMftFile: state.endpoint.hostDownloads.downloads.selectedMftFile
});
const mftContainer = Component.extend({
  tagName: 'box',
  classNames: ['mft-container'],
  accessControl: service()
});

export default connect(stateToComputed)(mftContainer);
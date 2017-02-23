import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import computed, { filterBy } from 'ember-computed-decorators';
import * as InteractionActions from 'recon/actions/interaction-creators';
import ReconEventTypes from 'recon/mixins/recon-event-types';
import ReconExport from 'recon/mixins/recon-export';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  files: data.files,
  status: data.fileExtractStatus,
  extractLink: data.fileExtractLink,
  eventType: data.eventType
});

const dispatchToActions = (dispatch) => ({
  extractFiles: () => dispatch(InteractionActions.extractFiles('FILES')),
  didDownloadFiles: () => dispatch(InteractionActions.didDownloadFiles())
});

const ExportFilesComponent = Component.extend(ReconEventTypes, ReconExport, {
  layout,

  @computed('isDownloading', 'selectedFiles.length')
  caption(isDownloading, count) {
    return isDownloading ?
      'Exporting...' : (count > 1) ?
        `Export Files (${count})` : 'Export File';
  },

  @filterBy('files', 'selected', true)
  selectedFiles: [],

  @computed('isDownloading', 'selectedFiles.length')
  isDisabled(isDownloading, count) {
    return !count || isDownloading;
  }
});

export default connect(stateToComputed, dispatchToActions)(ExportFilesComponent);

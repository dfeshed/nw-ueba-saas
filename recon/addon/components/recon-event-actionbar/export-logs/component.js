import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import * as InteractionActions from 'recon/actions/interaction-creators';
import ReconEventTypes from 'recon/mixins/recon-event-types';
import ReconExport from 'recon/mixins/recon-export';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  status: data.fileExtractStatus,
  extractLink: data.fileExtractLink,
  eventType: data.eventType
});

const dispatchToActions = (dispatch) => ({
  extractFiles: () => dispatch(InteractionActions.extractFiles('LOG')),
  didDownloadFiles: () => dispatch(InteractionActions.didDownloadFiles())
});

const DownloadLogsComponent = Component.extend(ReconEventTypes, ReconExport, {
  layout,

  @computed('isDownloading')
  caption: (isDownloading) => isDownloading ? 'Downloading...' : 'Download Logs'
});

export default connect(stateToComputed, dispatchToActions)(DownloadLogsComponent);

import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import * as InteractionActions from 'recon/actions/interaction-creators';
import ReconExport from 'recon/mixins/recon-export';
import layout from './template';
import { isLogEvent } from 'recon/selectors/event-type-selectors';

const { Component } = Ember;

const stateToComputed = ({ recon, recon: { data } }) => ({
  status: data.fileExtractStatus,
  extractLink: data.fileExtractLink,
  eventType: data.eventType,
  isLogEvent: isLogEvent(recon)
});

const dispatchToActions = (dispatch) => ({
  extractFiles: () => dispatch(InteractionActions.extractFiles('LOG')),
  didDownloadFiles: () => dispatch(InteractionActions.didDownloadFiles())
});

const DownloadLogsComponent = Component.extend(ReconExport, {
  layout,

  @computed('isDownloading')
  caption: (isDownloading) => isDownloading ? 'Downloading...' : 'Download Logs'
});

export default connect(stateToComputed, dispatchToActions)(DownloadLogsComponent);

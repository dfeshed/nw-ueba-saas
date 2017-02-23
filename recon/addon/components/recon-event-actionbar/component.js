import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  view: data.currentReconView.code
});

const reconEventActionbar = Component.extend({
  layout,
  classNames: ['recon-event-actionbar'],

  @computed('view')
  isTextView: (view) => view === RECON_VIEW_TYPES_BY_NAME.TEXT.code,

  @computed('view')
  isFileView: (view) => view === RECON_VIEW_TYPES_BY_NAME.FILE.code,

  @computed('view')
  isPacketView: (view) => view === RECON_VIEW_TYPES_BY_NAME.PACKET.code
});

export default connect(stateToComputed)(reconEventActionbar);
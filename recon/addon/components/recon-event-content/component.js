import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import {
  isLoading
} from 'recon/reducers/data-selectors';

import {
  isWebEmail
} from 'recon/reducers/meta/selectors';

import {
  isEmailView
} from 'recon/reducers/visuals/selectors';

import { inject as service } from '@ember/service';
import layout from './template';
import ReconPagerMixin from 'recon/mixins/recon-pager';

const stateToComputed = ({ recon }) => ({
  currentReconView: recon.visuals.currentReconView,
  isLoading: isLoading(recon),
  isMetaShown: recon.visuals.isMetaShown,
  reconData: recon.data,
  dataIndex: recon.data.index,
  isEmailView: isEmailView(recon),
  isWebEmail: isWebEmail(recon),
  eventTotal: recon.data.total
});

const EventContentComponent = Component.extend(ReconPagerMixin, {
  layout,
  classNameBindings: [':recon-event-content', 'isMetaShown:col-xs-8:col-xs-12'],
  tagName: 'vbox',
  i18n: service(),
  accessControl: service(),

  @computed('accessControl.hasReconAccess')
  missingPermissions(hasReconAccess) {
    return !hasReconAccess;
  },

  @computed('reconData.endpointId', 'reconData.eventId')
  classicWebReconPath(endpointId, eventId) {
    return `/investigation/${endpointId}/navigate/event/WEB/${eventId}`;
  }
});

export default connect(stateToComputed)(EventContentComponent);

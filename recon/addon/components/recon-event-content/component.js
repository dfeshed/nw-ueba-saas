import Ember from 'ember';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

import { RECON_VIEW_TYPES_BY_NAME } from '../../utils/reconstruction-types';
import layout from './template';

const {
  Component,
  inject: {
    service
  }
} = Ember;

const stateToComputed = ({ recon: { data } }) => ({
  currentReconView: data.currentReconView,
  contentError: data.contentError,
  contentLoading: data.contentLoading,
  headerLoading: data.headerLoading,
  eventId: data.eventId
});

const EventContentComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-content'],
  tagName: 'vbox',
  i18n: service(),

  /**
   * contentError is a code
   * if the code is 2, then it is an expected error condition for which
   * we message specifically.
   * if the code is not a 2, it is an unexpected error
   *
   * @public
   */
  @computed('contentError')
  errorMessage(errorCode) {
    if (errorCode === 2) {
      return this.get('i18n').t('recon.error.missingRecon', { id: this.get('eventId') });
    } else {
      return this.get('i18n').t('recon.error.generic');
    }
  },

  /**
   * Determines if the content is loading, results in spinner vs
   * a component view
   *
   * @public
   */
  @computed('currentReconView', 'contentLoading', 'headerLoading')
  isLoading({ code }, contentLoading, headerLoading) {
    // if content is loading, its loading
    if (contentLoading) {
      return true;
    }

    // if content is not loading, and its not the packet view,
    // we aren't waiting for anything anymore
    if (code !== RECON_VIEW_TYPES_BY_NAME.PACKET.code) {
      return false;
    }

    // if it is packet view, and the content is done loading
    // then headerLoading is the determining factor
    return headerLoading;
  }

});

export default connect(stateToComputed)(EventContentComponent);
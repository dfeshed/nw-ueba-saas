import Ember from 'ember';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

import layout from './template';

const {
  Component,
  inject: {
    service
  }
} = Ember;

const stateToComputed = ({ data }) => ({
  currentReconView: data.currentReconView,
  contentError: data.contentError,
  contentLoading: data.contentLoading,
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
  }
});

export default connect(stateToComputed)(EventContentComponent);
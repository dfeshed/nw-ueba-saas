import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { isPacketView } from 'recon/reducers/visuals/selectors';
import layout from './template';

const stateToComputed = ({ recon, recon: { data, header, visuals } }) => ({
  currentReconView: visuals.currentReconView,
  contentError: data.contentError,
  contentLoading: data.contentLoading,
  headerLoading: header.headerLoading,
  eventId: data.eventId,
  isMetaShown: visuals.isMetaShown,
  isPacketView: isPacketView(recon)
});

const EventContentComponent = Component.extend({
  layout,
  classNameBindings: [':recon-event-content', 'isMetaShown:col-xs-8:col-xs-12'],
  tagName: 'vbox',

  /**
   * contentError is a code
   * if the code is 2 or 13, then it is an expected error condition for which
   * we message specifically.
   * if the code is something else, it is an unexpected error
   * @public
   */
  @computed('contentError')
  errorMessage(errorCode) {
    let ret;
    switch (errorCode) {
      case 2:
        ret = this.get('i18n').t('recon.error.missingRecon', { id: this.get('eventId') });
        break;
      case 13:
      case 110:
        ret = this.get('i18n').t('recon.error.permissionError');
        break;
      case 115:
        ret = this.get('i18n').t('recon.error.sessionUnavailable');
        break;
      default:
        ret = this.get('i18n').t('recon.error.generic');
    }
    return ret;
  },

  /**
   * Determines if the content is loading, results in spinner vs
   * a component view
   *
   * @public
   */
  @computed('isPacketView', 'contentLoading', 'headerLoading')
  isLoading(isPacketView, contentLoading, headerLoading) {
    // if content is loading, its loading
    if (contentLoading) {
      return true;
    }

    // if content is not loading, and its not the packet view,
    // we aren't waiting for anything anymore
    if (!isPacketView) {
      return false;
    }

    // if it is packet view, and the content is done loading
    // then headerLoading is the determining factor
    return headerLoading;
  }

});

export default connect(stateToComputed)(EventContentComponent);

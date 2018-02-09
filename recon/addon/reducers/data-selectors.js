import { createSelector } from 'reselect';
import { lookup } from 'ember-dependency-lookup';
import { isPacketView } from 'recon/reducers/visuals/selectors';

const _contentError = (recon) => recon.data.contentError;
const _contentLoading = (recon) => recon.data.contentLoading;
const _headerLoading = (recon) => recon.header.headerLoading;
const _eventId = (recon) => recon.data.eventId;

/**
 * Use to determine if there was an error retrieving the content of a
 * reconstruction.
 * @param {number} contentError - An error code.
 * @return {boolean}
 * @public
 */
export const isContentError = createSelector(
  [_contentError],
  (contentError) => !!contentError
);

/**
 * If the code is 2 or 13/110, then it is an expected error condition for
 * which we message specifically. If the code is something else, it is an
 * unexpected error.
 * @param {number} contentError - An error code.
 * @param {number} eventId - Id of event.
 * @return {string} A localized error message.
 * @public
 */
export const errorMessage = createSelector(
  [_contentError, _eventId],
  (contentError, eventId) => {
    const i18n = lookup('service:i18n');
    let ret;
    switch (contentError) {
      case 2:
        ret = i18n.t('recon.error.missingRecon', { id: eventId });
        break;
      case 13:
      case 110:
        ret = i18n.t('recon.error.permissionError');
        break;
      default:
        ret = i18n.t('recon.error.generic');
    }
    return ret;
  }
);

/**
 * Determines if the content for a reconstruction is loading
 * @param {boolean} isPacketLoading
 * @param {boolean} contentLoading
 * @param {boolean} headerLoading
 * @return {boolean}
 * @public
 */
export const isLoading = createSelector(
  [isPacketView, _contentLoading, _headerLoading],
  (isPacketView, contentLoading, headerLoading) => {
    // If content is loading, its loading
    if (contentLoading) {
      return true;
    }
    // If content is not loading, and its not the packet view,
    // we aren't waiting for anything anymore.
    if (!isPacketView) {
      return false;
    }
    // If it is packet view, and the content is done loading
    // then headerLoading is the determining factor.
    return headerLoading;
  }
);

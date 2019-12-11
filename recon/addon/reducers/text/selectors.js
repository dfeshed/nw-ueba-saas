import { createSelector } from 'reselect';

import getMetaKeysByEventType from './limited-meta';
import {
  isRequestShown,
  isResponseShown,
  isTextView
} from 'recon/reducers/visuals/selectors';
import { eventType, isEndpointEvent, isNetworkEvent } from 'recon/reducers/meta/selectors';

const _textContent = (recon) => recon.text.textContent;
const _canNext = (recon) => recon.text.canNext;
const _canPrevious = (recon) => recon.text.canPrevious;
const _canLast = (recon) => recon.text.canLast;
const _isTextPageLoading = (recon) => recon.text.isTextPageLoading;
const _renderIds = (recon) => recon.text.renderIds;
const _meta = (recon) => recon.meta.meta;

/**
 * Returns an Array of event meta Arrays that have the following structure:
 * [ <name>, <value> ]
 * @param {object[]} eventMeta The meta for an event
 * @param {string} key The meta key to look for
 * @return {object[]} An Array of Arrays that contain the event meta
 * @private
 */
const _getEventMetaByKey = (eventMeta, key) => eventMeta && eventMeta.filter((meta) => meta[0] === key);

const _isString = (s) => Object.prototype.toString.call(s) === '[object String]';

export const hasTextContent = createSelector(
  [_textContent],
  (textContent) => !!textContent && textContent.length > 0
);

export const canGoToNextPage = createSelector(
  [_canNext, _isTextPageLoading],
  (canNext, isTextPageLoading) => canNext && !isTextPageLoading
);

export const canGoToPreviousPage = createSelector(
  [_canPrevious, _isTextPageLoading],
  (canPrevious, isTextPageLoading) => canPrevious && !isTextPageLoading
);

export const canGoToLastPage = createSelector(
  [_canLast, _isTextPageLoading],
  (canLast, isTextPageLoading) => canLast && !isTextPageLoading
);

export const hasRenderIds = createSelector(
  [_renderIds],
  (renderIds) => !!renderIds && renderIds.length > 0
);
/**
 * A selector that returns an array of those items that are renderable
 * Exporting it for testing purposes
 * @public
 */
export const _renderableText = createSelector(
  [_textContent, isRequestShown, isResponseShown, isNetworkEvent],
  (textContent, isRequestShown = true, isResponseShown = true, isNetworkEvent) => {

    // textContent can be null/empty, eject
    if (!textContent || textContent.length === 0) {
      return [];
    }

    // If it is not a network event, no need to filter content
    // based on request/response
    if (!isNetworkEvent) {
      return textContent;
    }

    // if showing all textContent, just return them
    if (isRequestShown && isResponseShown) {
      return textContent;
    }

    // we're not showing req or res, so let's filter them out
    return textContent.filter((t) => {
      return (t.side === 'request' && isRequestShown) ||
             (t.side === 'response' && isResponseShown);
    });
  }
);

/**
 * Counts the number of occurances of a meta string within some text.
 * @param {Object} meta The meta to look for
 * @param {string} text The text to look through
 * @return {number} The count
 * @private
 */
const _findMetaCountInString = (meta, text) => {
  let matches;
  if (_isString(meta) && _isString(text)) {
    // Escape RegEx special characters
    const pattern = meta.replace(/[-[\]{}()*+?.,\\^$|#]/g, '\\$&');
    const regex = new RegExp(pattern, 'gi');
    matches = text.match(regex);
  }
  return matches ? matches.length : 0;
};

/*
 * Do we have text content but no renderable content
 * because the user has hidden it all?
 */
export const allDataHidden = createSelector(
  [hasTextContent, _renderableText],
  (hasTextContent, renderableText) => hasTextContent && renderableText.length === 0
);

/**
 * A selector that returns an array of those items that are to be rendered
 *
 * @public
 */
export const renderedText = createSelector(
  [_renderableText, _renderIds],
  (renderableText, renderIds) => {

    // renderIds can be null/empty, eject
    if (!renderIds || renderIds.length === 0) {
      return [];
    }

    // just want the packets with their id chosen to be rendered
    return renderableText.filter((t) => renderIds.includes(t.firstPacketId));
  }
);

/**
 * A selector that goes through the renderedText and counts the number of
 * occurances for each meta that's relevent to the type of event.
 * @return {object[]} The number of occurances of each meta within the
 +                    rendered text.
 * @public
 */
export const metaHighlightCount = createSelector(
  [renderedText, isTextView, eventType, isEndpointEvent, _meta],
  (renderedText, isTextView, eventType, isEndpointEvent, eventMeta) => {
    const metaCounts = [];
    if (isTextView && renderedText && renderedText.length > 0) {
      const eventTypeName = isEndpointEvent ? 'endpoint' : eventType.name.toLowerCase();
      const metaKeys = getMetaKeysByEventType(eventTypeName);
      const fullText = renderedText.map((p) => p.text).join(' ');
      metaKeys.forEach((metaKey) => {
        const metas = _getEventMetaByKey(eventMeta, metaKey) || [];
        for (let i = 0; i < metas.length; i++) {
          const meta = metas[i];
          if (meta && meta.length === 2) {
            const count = _findMetaCountInString(meta[1], fullText);
            metaCounts.push({
              name: metaKey,
              value: meta[1],
              count
            });
          }
        }
      });
    }
    return metaCounts;
  }
);

/*
 * Iterates over all the visible packets and determines
 * if there is any payload for this event
 */
export const eventHasPayload = createSelector(
  [renderedText],
  (textEntries) => {
    if (!textEntries || textEntries.length === 0) {
      return false;
    }

    const aTextEntryWithPayload = textEntries.find((t) => t.text && t.text.length > 0);

    return !!aTextEntryWithPayload;
  }
);

// the number of text items that are destined to be rendered
export const numberOfRenderableTextEntries = createSelector(
  [hasTextContent, _renderableText],
  (hasTextContent, renderableText) => {
    if (hasTextContent) {
      return renderableText.length;
    }

    return 0;
  }
);

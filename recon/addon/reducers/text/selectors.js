import reselect from 'reselect';
import getMetaKeysByEventType from './limited-meta';
import {
  isRequestShown,
  isResponseShown,
  isTextView
} from 'recon/reducers/visuals/selectors';
import {
  eventType,
  isEndpointEvent
} from 'recon/reducers/meta/selectors';

const { createSelector } = reselect;
const textContent = (recon) => recon.text.textContent;
const renderIds = (recon) => recon.text.renderIds;
const meta = (recon) => recon.meta.meta;

/**
 * Returns an event meta array that has the following structure:
 * [ <name>, <value> ]
 * @param {object[]} eventMeta The meta for an event.
 * @param {string} key The meta key to look for
 * @return {object[]}
 * @private
 */
const _getEventMetaByKey = (eventMeta, key) => eventMeta.find((meta) => meta[0] === key);

const _isString = (s) => Object.prototype.toString.call(s) === '[object String]';

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

export const hasTextContent = createSelector(
  [textContent],
  (textContent) => textContent && textContent.length > 0
);

/**
 * A selector that tells you if the content has been retrieved.
 * The `textContent` property can at different times be null, an empty array or
 * a populated array. An empty array means the event has no text content. If
 * `textContent` is `null`, then it is still being fetched.
 * @public
 */
export const contentRetrieved = createSelector(
  [textContent],
  (textContent) => textContent !== null
);

/**
 * A selector that returns an array of those items that are renderable
 *
 * @public
 */
export const renderableText = createSelector(
  [textContent, isRequestShown, isResponseShown],
  (textContent, isRequestShown, isResponseShown) => {

    // textContent can be null/empty, eject
    if (!textContent || textContent.length === 0) {
      return [];
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

/*
 * Do we have text content but no renderable content
 * because the user has hidden it all?
 */
export const allDataHidden = createSelector(
  [hasTextContent, renderableText],
  (hasTextContent, renderableText) => hasTextContent && renderableText.length === 0
);

/**
 * A selector that returns an array of those items that are to be rendered
 *
 * @public
 */
export const renderedText = createSelector(
  [renderableText, renderIds],
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
 * @public
 */
export const metaHighlightCount = createSelector(
  [renderedText, isTextView, eventType, isEndpointEvent, meta],
  (renderedText, isTextView, eventType, isEndpointEvent, eventMeta) => {
    const metaCounts = [];
    if (isTextView && renderedText && renderedText.length > 0) {
      const eventTypeName = isEndpointEvent ? 'endpoint' : eventType.name.toLowerCase();
      const metaKeys = getMetaKeysByEventType(eventTypeName);
      const fullText = renderedText.map((p) => p.text).join(' ');
      metaKeys.forEach((metaKey) => {
        const meta = _getEventMetaByKey(eventMeta, metaKey);
        if (meta && meta.length === 2) {
          const count = _findMetaCountInString(meta[1], fullText);
          metaCounts.push({
            name: metaKey,
            count
          });
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
  [hasTextContent, renderableText],
  (hasTextContent, renderableText) => {
    if (hasTextContent) {
      return renderableText.length;
    }

    return 0;
  }
);

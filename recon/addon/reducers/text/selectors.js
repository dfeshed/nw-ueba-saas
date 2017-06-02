import reselect from 'reselect';

import { isRequestShown, isResponseShown } from 'recon/reducers/visuals/selectors';

const { createSelector } = reselect;
const textContent = (recon) => recon.text.textContent;
const renderIds = (recon) => recon.text.renderIds;
const metaToHighlight = (recon) => recon.text.metaToHighlight;

export const hasTextContent = createSelector(
  [textContent],
  (textContent) => textContent && textContent.length > 0
);

// textContent can at different times be null, an empty array
// or a populated array. An empty array means the event has
// no text content. If textContent is null, then it is still
// being fetched.
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


export const totalMetaToHighlight = createSelector(
  [renderedText, metaToHighlight],
  (textEntries, metaToHighlight) => {
    if (!metaToHighlight || textEntries.length === 0) {
      return 0;
    }
    const metaStringRegex = new RegExp(String(metaToHighlight.value), 'gi');
    const fullText = textEntries.map((p) => p.text).join(' ');
    const matches = fullText.match(metaStringRegex);
    const totalMatches = matches ? matches.length : 0;
    return totalMatches;
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

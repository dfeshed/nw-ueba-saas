import reselect from 'reselect';

const { createSelector } = reselect;
const textContent = (recon) => recon.text.textContent;
const renderIds = (recon) => recon.text.renderIds;
const metaToHighlight = (recon) => recon.text.metaToHighlight;
const isRequestShown = (recon) => recon.visuals.isRequestShown;
const isResponseShown = (recon) => recon.visuals.isResponseShown;

// textContent can at different times be null, an empty array
// or a populated array. An empty array means the event has
// no text content. If textContent is null, then it is still
// being fetched.
export const contentRetrieved = createSelector(
  [textContent],
  (textContent) => textContent !== null
);

/**
 * A selector that returns a sorted Array of all visible text.
 * @private
 */
export const visibleText = createSelector(
  [textContent, renderIds, isRequestShown, isResponseShown],
  (textContent, renderIds, isRequestShown, isResponseShown) => {

    // textContent can be null/empty, eject
    // renderIds can be null/empty, eject
    if (!textContent || textContent.length === 0 || !renderIds || renderIds.length === 0) {
      return [];
    }

    // just want the packets with their id chosen to be rendered
    const renderedText = textContent.filter((p) => renderIds.includes(p.firstPacketId));

    // if showing all textContent, just return them
    if (isRequestShown && isResponseShown) {
      return renderedText;
    }

    // we're not showing req or res, so let's filter them out
    return renderedText.filter((t) => {
      return (t.side === 'request' && isRequestShown) ||
             (t.side === 'response' && isResponseShown);
    });
  }
);

export const totalMetaToHighlight = createSelector(
  [visibleText, metaToHighlight],
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
  [visibleText],
  (textEntries) => {
    if (!textEntries || textEntries.length === 0) {
      return false;
    }

    const aTextEntryWithPayload = textEntries.find((t) => t.text && t.text.length > 0);

    return !!aTextEntryWithPayload;
  }
);

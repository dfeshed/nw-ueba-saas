import reselect from 'reselect';

const { createSelector } = reselect;
const textContent = (recon) => recon.text.textContent;
const metaToHighlight = (recon) => recon.text.metaToHighlight;
const isRequestShown = (recon) => recon.visuals.isRequestShown;
const isResponseShown = (recon) => recon.visuals.isResponseShown;

/**
 * A selector that returns a sorted Array of all visible text.
 * @private
 */
export const visibleText = createSelector(
  [textContent, isRequestShown, isResponseShown],
  (textContent, isRequestShown, isResponseShown) => {

    // textContent can be null, eject
    if (!textContent) {
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

    // text is an array, gotta join it up
    const aTextEntryWithPayload =
      textEntries.find((t) => t.text && t.text.join('').length > 0);

    return !!aTextEntryWithPayload;
  }
);


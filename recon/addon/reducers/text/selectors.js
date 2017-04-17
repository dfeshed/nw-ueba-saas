import reselect from 'reselect';

const { createSelector } = reselect;
const textContent = (recon) => recon.text.textContent;
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
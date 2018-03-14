import reselect from 'reselect';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _headerError = (state) => state.header.headerError;
const _headerErrorCode = (state) => state.header.headerErrorCode;
const _headerItems = (state) => state.header.headerItems || [];

export const packetTotal = createSelector(
  [_headerItems],
  (headerItems) => {
    const found = headerItems.findBy('name', 'packetCount');
    if (found) {
      return found.value;
    }
  }
);

export const headerErrorMessage = createSelector(
  [_headerError, _headerErrorCode],
  (headerError, headerErrorCode) => {
    const i18n = lookup('service:i18n');
    // We're not returning an error string for code 110 because it's handled in
    // the body of the recon, and can not happen seperately in the header.
    return (headerErrorCode === 110) ? '' : i18n.t('recon.error.generic');
  }
);
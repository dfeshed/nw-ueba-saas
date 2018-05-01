import reselect from 'reselect';
import { lookup } from 'ember-dependency-lookup';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
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
  [_headerErrorCode],
  (headerErrorCode) => {
    const errorObj = handleInvestigateErrorCode({ code: headerErrorCode });
    // We're not returning an error string for code 110 because it's handled in
    // the body of the recon, and can not happen seperately in the header.
    return lookup('service:i18n').t(errorObj.messageLocaleKey);
  }
);

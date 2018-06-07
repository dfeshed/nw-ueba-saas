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

    if (!errorObj) {
      return;
    }

    const { errorCode, type, messageLocaleKey } = errorObj;

    return lookup('service:i18n').t(messageLocaleKey, { errorCode, type });
  }
);

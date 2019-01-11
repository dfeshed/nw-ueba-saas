import { selectors as listSelectors } from './selectors';

export const selectors = {
  tooltip: '.rsa-context-tooltip__header',
  hostName: '[test-id=eventHostnameValue]',
  row: listSelectors.row,
  clearButton: listSelectors.clearButton
};

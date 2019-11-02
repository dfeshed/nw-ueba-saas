import {
  CLOSE_PAREN,
  OPEN_PAREN,
  OPERATOR_AND,
  OPERATOR_OR
} from 'investigate-events/constants/pill';

export const isDeletingSingleFocusedParenSet = (pills, isKeyPress = false) => {
  return pills.length === 2 &&
    pills.every((d) => d.type === OPEN_PAREN || d.type === CLOSE_PAREN) && // are parens
    pills.some((d) => d.isFocused) && // at least one pill is focused
    pills.every((d) => isKeyPress || !d.isSelected); // selected using key press actions or none are selected
};

/**
 * Checks if a pill or operator has to be deleted
 *
 * @param {*} deleteIds array of  pill ids already marked to deleted.
 * @param {*} pill check where this pill is to be deleted
 * @param {*} idx index of the current pill
 * @param {*} pillsData array of all the pills
 * @returns boolean
 */

export const isPillOrOperatorToBeDelete = (deleteIds, pill, idx, pillsData) => {
  const shouldDeletePill = deleteIds.includes(pill.id);
  if (shouldDeletePill) {
    // quick exit
    return true;
  }
  // When deleted pills are between two selected parens then the logical operator before open paren is deleted but the one after the close paren is retained to maintain proper syntax
  if (includeLogicalOpAfterParens(deleteIds, pill, idx, pillsData)) {
    return false;
  }
  const nextPill = pillsData[idx + 1];
  const prevPill = pillsData[idx - 1];
  const beforePrevPill = pillsData[idx - 2];
  const shouldDeleteNextPill = nextPill && deleteIds.includes(nextPill.id);
  const shouldDeletePrevPill = prevPill && deleteIds.includes(prevPill.id) && !_isLogicalOperator(beforePrevPill);
  return (_isLogicalOperator(pill) && (shouldDeleteNextPill || shouldDeletePrevPill));
};

/**
 * Checks if the elements deleted are within in a set of selected parens.
 *
 * @param {*} deleteIds array of  pill ids already marked to deleted.
 * @param {*} pill check where this pill is to be deleted
 * @param {*} idx index of the current pill
 * @param {*} pillsData array of all the pills
 * @returns boolean
 */
export const includeLogicalOpAfterParens = (deleteIds, pill, idx, pillsData) => {
  const prevPill = pillsData[idx - 1];
  // if the pill is not a logical operator or if the previous pill is not close paren
  // then we can avoid all below computations.
  if (!_isLogicalOperator(pill) || !prevPill || prevPill.type !== CLOSE_PAREN) {
    return false;
  }
  const beforePrevPill = pillsData[idx - 2];
  const [firstDeleteId] = deleteIds;
  const firstPillDeleted = pillsData.find((pill) => pill.id === firstDeleteId);
  // making sure Multiple pills are being deleted, eg (),(pill), (pill ..)
  const isRemovingMultiplePills = deleteIds.length > 1 && prevPill && beforePrevPill &&
  deleteIds.includes(prevPill.id) && deleteIds.includes(beforePrevPill.id);
  const isFirstPillOpenParen = !!firstPillDeleted && firstPillDeleted.type === OPEN_PAREN && firstPillDeleted.twinId === prevPill.twinId;
  const areBothParensSelected = !!firstPillDeleted && prevPill.isSelected && firstPillDeleted.isSelected;
  return isRemovingMultiplePills && isFirstPillOpenParen && areBothParensSelected;
};

const _isLogicalOperator = (pill) => pill && (pill.type === OPERATOR_AND || pill.type === OPERATOR_OR);
import { OPERATOR_AND, OPERATOR_OR, TEXT_FILTER } from 'investigate-events/constants/pill';

// Adds a property to operators that are either directly before a text pill,
// or directly after a text pill that is the first pill
export const markTextPillAttachedOperators = (pills) => {
  return pills.map((pill, idx, pills) => {
    if (idx === 1) {
      if (pill.type === OPERATOR_AND || pill.type === OPERATOR_OR) {
        const [ firstPill,, nextPill ] = pills;
        if (firstPill.type === TEXT_FILTER || nextPill?.type === TEXT_FILTER) {
          return { ...pill, isTextPillAttached: true };
        } else {
          return { ...pill, isTextPillAttached: false };
        }
      }
      return pill;
    } else {
      if (pill.type === OPERATOR_AND || pill.type === OPERATOR_OR) {
        const nextPill = pills[idx + 1];
        if (nextPill?.type === TEXT_FILTER) {
          return { ...pill, isTextPillAttached: true };
        } else {
          return { ...pill, isTextPillAttached: false };
        }
      }
      return pill;
    }
  });
};
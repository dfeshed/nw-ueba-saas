import {
  CLOSE_PAREN,
  COMPLEX_FILTER,
  OPEN_PAREN,
  QUERY_FILTER,
  TEXT_FILTER
} from 'investigate-events/constants/pill';
import { warn } from '@ember/debug';

const _stringify = (pills) => {
  return pills.reduce((acc, cur, idx) => {
    const sep = (idx === 0) ? '' : '-';
    let str = '';
    if (cur.type === QUERY_FILTER) {
      const { meta, operator, value } = cur;
      str = `${meta}${operator}${(value) ? value : ''}`;
    } else if (cur.type === OPEN_PAREN) {
      str = '(';
    } else if (cur.type === CLOSE_PAREN) {
      str = ')';
    } else if (cur.type === COMPLEX_FILTER) {
      str = cur.complexFilterText;
    } else if (cur.type === TEXT_FILTER) {
      str = cur.searchTerm;
    } else {
      warn('QUERY_HASH::createQueryHash() - Unknown filter type', { id: 'missing-type' });
    }
    return `${acc}${sep}${str}`;
  }, '');
};

export const createQueryHash = function(service, startTime, endTime, pills) {
  return `${service}-${startTime}-${endTime}-${_stringify(pills)}`;
};
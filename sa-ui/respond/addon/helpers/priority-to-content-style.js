import { helper } from '@ember/component/helper';

export function priorityToContentStyle([priority]) {
  priority = priority || '';
  return priority === 'CRITICAL' ? 'danger' : priority.toLowerCase();
}

export default helper(priorityToContentStyle);

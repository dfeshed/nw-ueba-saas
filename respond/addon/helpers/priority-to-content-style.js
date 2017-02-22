import Ember from 'ember';

const { Helper: { helper } } = Ember;

export function priorityToContentStyle([priority]) {
  return priority === 'CRITICAL' ? 'danger' : priority.toLowerCase();
}

export default helper(priorityToContentStyle);

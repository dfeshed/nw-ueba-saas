import { helper } from '@ember/component/helper';

const closedIncidentStatuses = ['CLOSED', 'CLOSED_FALSE_POSITIVE'];

export function isIncidentClosed(status) {
  return closedIncidentStatuses.includes(status);
}

export default helper(function([status]) {
  return isIncidentClosed(status);
});

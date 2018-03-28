import data from '../query/data';

export default {
  subscriptionDestination: '/user/queue/incident/escalate',
  requestDestination: '/ws/respond/incident/escalate',
  message(frame) {
    const body = JSON.parse(frame.body);
    const found = data.filter((incident) => incident.id === body.incidentId);
    const incident = found.length ? found[0] : {};
    incident.escalationStatus = 'ESCALATED';
    return {
      code: 0,
      data: incident
    };
  }
};
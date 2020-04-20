import data from '../query/data';

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/incidents/archer/send',
  requestDestination: '/ws/respond/incidents/archer/send',
  message(frame) {
    const body = JSON.parse(frame.body);
    const found = data.filter((incident) => incident.id === body.incidentId);
    const incident = found.length ? found[0] : {};
    incident.sentToArcher = true;
    return {
      code: 0,
      data: incident,
      meta: {
        archerIncidentId: '12321349'
      }
    };
  }
};

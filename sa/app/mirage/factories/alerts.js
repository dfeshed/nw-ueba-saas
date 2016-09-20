import Mirage, { faker } from 'ember-cli-mirage';

export default Mirage.Factory.extend({
  id(i) {
    return `INC- ${i}`;
  },
  receivedTime: 1474091559854,
  status: 'GROUPED_IN_INCIDENT',
  errorMessage: null,
  originalHeaders: null,
  originalRawAlert: null,
  originalAlert: null,
  alert(i) {
    return {
      severity: faker.list.cycle(40, 50, 100)(i),
      related_links: [],
      host_summary: '-',
      user_summary: [],
      risk_score(i) {
        return faker.list.cycle(40, 50, 100)(i);
      },
      groupby_domain: '4554mb.ru',
      source: 'Event Stream Analysis',
      groupby_destination_port: '',
      groupby_source_country: '',
      groupby_destination_country: '',
      relationships: [],
      signature_id: 'Some rule',
      groupby_filename: '',

      groupby_data_hash: '',
      groupby_destination_ip: '',
      name: 'P2P software as detected by an Intrusion detection device',
      numEvents: 1
    };
  },
  incidentId(i) {
    return `INC- ${i}`;
  },
  partOfIncident: true,
  incidentCreated: 1474091560367,
  timestamp: 1471341807482
});

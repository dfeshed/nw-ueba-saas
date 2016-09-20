import Mirage, { faker } from 'ember-cli-mirage';

export default Mirage.Factory.extend({
  id(i) {
    return `INC- ${i}`;
  },
  name(i) {
    return `Name ${i}`;
  },
  summary(i) {
    return `Summary ${i}`;
  },
  priority(i) {
    return faker.list.cycle('CRITICAL', 'HIGH', 'MEDIUM', 'LOW')(i);
  },
  prioritySort(i) {
    return faker.list.cycle(3,2,1,0)(i);
  },
  riskScore(i) {
    return faker.list.random(30,70,90,88)(i);
  },
  status(i) {
    return faker.list.cycle('NEW', 'CLOSED', 'IN_PROGRESS')(i);
  },
  statusSort(i) {
    return faker.list.cycle(0, 5, 1)(i);
  },
  alertCount(i) {
    return faker.list.random(10, 500, 80)(i);
  },
  averageAlertRiskScore(i) {
    return faker.list.random(10, 500, 80)(i);
  },
  sealed: true,
  totalRemediationTaskCount: 0,
  openRemediationTaskCount: 0,
  created: 1472157480611,
  lastUpdated: 1472157480611,
  lastUpdatedByUser: null,
  assignee: { 'id': 1, 'firstName': 'Ian', 'lastName': 'RSA', 'emailAddress': 'test@rsa.com' },
  sources(i) {
    return faker.list.random(['Event Stream Analysis'], ['Event Stream Analysis', 'ECAT'], ['ECAT'])(i);
  },
  ruleId: '563b8a8e3004657082c3bd89',
  firstAlertTime: 1445940207482,
  timeWindowExpiration: 1445943807482,
  groupByValues: ['4554mb.ru'],
  categories: [],
  notes: null,
  createdBy: 'Suspected Command & Control Communication By Domain',
  breachExportStatus: 'NONE',
  breachData: null,
  breachTag: null,
  deletedAlertCount: 0,
  groupByDomain: null,
  enrichment: null,
  eventCount: 1,
  createdFromRule: true
});

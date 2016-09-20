import Ember from 'ember';

const { Object: EmberObject, computed } = Ember;

export default EmberObject.extend({
  customerRiskyFeedbackPercentageTrend: computed(() => []),
  customerPercentageTrend: computed(() => []),
  feedback: null,
  customerInvestigatedPercentageTrend: computed(() => []),
  customerPercentage: 0.0,
  customerInvestigatedPercentage: 0.0,
  risk: null,
  unsafeModulesDownloaded: 'NONE',
  riskReasonTypeList: computed(() => []),
  customerRiskyFeedbackPercentage: 0.0,
  customerNotRiskyFeedbackPercentage: 0.0,
  unsafeModulesCommunicated: computed(() => []),
  relatedDomains: computed(() => []),
  customerNotRiskyFeedbackPercentageTrend: computed(() => []),
  riskScore: 0,
  id: '',
  firstSeen: 0
});
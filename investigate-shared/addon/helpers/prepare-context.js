import { helper } from '@ember/component/helper';

export function prepareContext([context, riskPanelActiveTab]) {
  let { resultList, resultMeta } = '';
  const activeTab = riskPanelActiveTab === 'ALERT' ? 'Alerts' : 'Incidents';
  if (context && context[0][activeTab]) {
    resultList = context[0][activeTab].resultList;
    resultMeta = context[0][activeTab].resultMeta;
  }
  if (resultList) {
    if (activeTab === 'Alerts') {
      resultList = resultList.map((alert) => {
        const incident = alert.incidentId;
        return { ...alert.alert, incident };
      });
    }
  }
  return { resultList, resultMeta };
}

export default helper(prepareContext);
import moment from 'moment';
import windowProxy from 'component-lib/utils/window-proxy';
import _ from 'lodash';

const _prepareMetaFormatMap = (language) => {
  if (!language) {
    return {};
  }
  return language.reduce(function(hash, { metaName, format }) {
    hash[metaName] = format;
    return hash;
  }, {});
};

const _buildQuery = (conditions = [], metaFormatMap = {}) => {
  return conditions.map((condition) => {
    const metaFormat = metaFormatMap[condition.meta];
    const { meta, value, operator } = condition;
    // if the metavalue already starts with a quote, don't add additional quotes
    //  e.g 'a``j1.[]1restonvirginia.sys' already has quotes around it, so not adding any quotes
    //  e.g a``j1.[]1restonvirginia.sys does not have outer quotes, so needs quotes or else classic blows up
    const surroundInQuotes = String(metaFormat).toLowerCase() === 'text' && value.trim().search(/['"]/g) !== 0;
    const valueEncoded = surroundInQuotes ? `'${String(value)}'` : value;
    return `${meta} ${operator} ${valueEncoded}`;
  }).join(' && ');
};

export const buildInvestigateUrl = (selected, queryOperator, contextDetails, discardParentQuery) => {
  const { metaName } = selected;
  let { metaValue } = selected;
  if (typeof metaValue === 'string') {
    // change metavalue NT Service\MSSQLSERVER to NT Service\\MSSQLSERVER so that on pivoting to classic, the query does not blow up.
    metaValue = metaValue.replace(/\\/g, '\\\\');
    // change metavalue 'eu'Tl' to 'eu\'Tl'
    metaValue = metaValue.replace(/['"]/g, '\\\'');
  }
  const { endpointId, startTime, endTime, queryConditions, language } = contextDetails;
  const metaFormatMap = _prepareMetaFormatMap(language);
  // parentQuery is the already existing meta filters on the page
  let parentQuery = discardParentQuery ? '' : _buildQuery(queryConditions, metaFormatMap);
  if (parentQuery !== '') {
    parentQuery = `(${parentQuery}) && `;
  }
  const newQuery = _buildQuery([{ meta: metaName, value: metaValue, operator: queryOperator }], metaFormatMap);
  // classic does decodeURI( decodeURIComponent(pillA) + ' && ' + decodeURIComponent(pillB)...)
  // so encode the query in that pattern
  const query = encodeURI(encodeURIComponent(parentQuery.concat(newQuery)));
  const formattedStartDate = moment(startTime > 0 ? startTime * 1000 : startTime).tz('utc').format();
  const formattedEndDate = moment(endTime > 0 ? endTime * 1000 : endTime).tz('utc').format();
  return `/investigation/endpointid/${endpointId}/navigate/query/${query}/date/${formattedStartDate}/${formattedEndDate}`;
};

export const buildHostsUrl = (selected, contextDetails) => {
  const metaFormatMap = _prepareMetaFormatMap(contextDetails.language);
  const query = _buildQuery([{ meta: selected.metaName, value: selected.metaValue, operator: '=' }], metaFormatMap);
  return `/investigate/hosts?query=${encodeURIComponent(query)}`;
};

/**
 * Following function will build investigate event analysis url for different drilling options.
 * @public
 */
export const buildEventAnalysisUrl = (selected, queryOperator, contextDetails, refocus) => {
  const metaFormatMap = _prepareMetaFormatMap(contextDetails.language);
  const href = windowProxy.currentUri().split('?');
  const queryParam = (href.length > 1) ? href[1] : '';
  const queryParamArray = queryParam.split('&');
  const mf = queryParamArray.find((param) => _.startsWith(param, 'mf='));
  // Need to close recon for all scenario.
  let filteredParams = queryParamArray.filter((param) => !_.startsWith(param, 'eid=') && !_.startsWith(param, 'mf='));
  // queryParam = queryParam.replace(/eid=/, '');
  const query = _buildQuery([{ meta: selected.metaName, value: selected.metaValue, operator: queryOperator }], metaFormatMap);
  const match = mf && mf.match(/mf=(.*)/);
  const metaFilter = match && match[1] ? match[1] : null;
  let mfToPass = null;
  let queryParamToPass = null;

  if (!metaFilter || refocus) {
    if (refocus) {
      // if refocus remove pdhash because refocus requires a replacement of existing filters
      filteredParams = filteredParams.filter((param) => !_.startsWith(param, 'pdhash='));
    }
    // If no meta filter add new one.
    mfToPass = `mf=${encodeURI(encodeURIComponent(query))}`;
  } else {
    // If further need to drill add to existing meta filter value.
    mfToPass = `mf=${metaFilter}/${encodeURI(encodeURIComponent(query))}`;
  }

  queryParamToPass = filteredParams.concat(mfToPass).join('&');

  return `/investigate/events?${queryParamToPass}`;
};

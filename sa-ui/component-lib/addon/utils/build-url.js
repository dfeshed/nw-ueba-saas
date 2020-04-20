import moment from 'moment';
import _ from 'lodash';

import { windowProxy } from 'component-lib/utils/window-proxy';

const _prepareMetaFormatMap = (language) => {
  if (!language) {
    return {};
  }
  return language.reduce(function(hash, { metaName, format }) {
    hash[metaName] = format;
    return hash;
  }, {});
};

export const buildQuery = (conditions = [], metaFormatMap = {}) => {
  return conditions.map((condition) => {
    const metaFormat = metaFormatMap[condition.meta];
    const { meta, value, operator } = condition;
    let valueEncoded = value;
    if (String(metaFormat).toLowerCase() === 'text') {
      // escape `\`. ex: metavalue NT Service\MSSQLSERVER to NT Service\\MSSQLSERVER.
      const escapedValues = value.replace(/\\/g, '\\\\');
      const surroundInQuotes = escapedValues.trim().search(/['"]/g) !== 0;
      // if the metavalue already starts with a quote, don't add additional quotes
      //  e.g 'a``j1.[]1restonvirginia.sys' already has quotes around it, so not adding any quotes
      //  e.g a``j1.[]1restonvirginia.sys does not have outer quotes, so needs quotes or else classic blows up
      surroundInQuotes ? valueEncoded = `'${String(escapedValues)}'` : valueEncoded = escapedValues;
    }
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
  let parentQuery = discardParentQuery ? '' : buildQuery(queryConditions, metaFormatMap);
  if (parentQuery !== '') {
    parentQuery = `(${parentQuery}) && `;
  }
  const newQuery = buildQuery([{ meta: metaName, value: metaValue, operator: queryOperator }], metaFormatMap);
  // classic does decodeURI( decodeURIComponent(pillA) + ' && ' + decodeURIComponent(pillB)...)
  // so encode the query in that pattern
  const query = encodeURI(encodeURIComponent(parentQuery.concat(newQuery)));
  const formattedStartDate = moment(startTime > 0 ? startTime * 1000 : startTime).tz('utc').format();
  const formattedEndDate = moment(endTime > 0 ? endTime * 1000 : endTime).tz('utc').format();
  return `/investigation/endpointid/${endpointId}/navigate/query/${query}/date/${formattedStartDate}/${formattedEndDate}`;
};

export const buildHostsUrl = (selected, contextDetails) => {
  const metaFormatMap = _prepareMetaFormatMap(contextDetails.language);
  const query = buildQuery([{ meta: selected.metaName, value: selected.metaValue, operator: '=' }], metaFormatMap);
  return `/investigate/hosts?query=${encodeURIComponent(query)}`;
};

/**
 * Constructs a Classic url based on the parameters passed.
 * @public
 */
export const classicEventsURL = ({
  endTime,
  startTime,
  timeRangeType,
  serviceId,
  pillDataHashes,
  textSearchTerm,
  mid1,
  mid2,
  startCollectionTime,
  endCollectionTime
}) => {
  const pillHash = extractHashWithoutTextHash(pillDataHashes);
  const formattedStartDate = moment(startTime > 0 ? startTime * 1000 : startTime).tz('utc').format();
  const formattedEndDate = moment(endTime > 0 ? endTime * 1000 : endTime).tz('utc').format();
  let searchTerm;
  if (textSearchTerm) {
    searchTerm = encodeURIComponent(textSearchTerm.searchTerm);
  }
  return `investigation/${serviceId}/events/${pillHash}date/${formattedStartDate}/${formattedEndDate}?mid1=${mid1}&mid2=${mid2}&lastCollectionDate=${endCollectionTime}&startCollectionDate=${startCollectionTime}&timeRangeType=${timeRangeType}&search=${searchTerm}`;
};

/**
 * Given a hash array, extracts hashes, excluding text hash and constructs a comma separated string.
 * @public
 */
export const extractHashWithoutTextHash = (hashesArr) => {
  let pillHash = '';
  if (!hashesArr) {
    return pillHash;
  } else if (hashesArr.length > 0) {
    pillHash = hashesArr.join().replace(/\u02F8(.)*\u02F8/, '').replace(/^,|,$/g, '').replace(/,,/g, ',');
    if (pillHash.length > 0) {
      pillHash = pillHash.concat('/');
    }
  }
  return pillHash;
};

/**
 * Following function will build investigate event analysis url for different drilling options.
 * @public
 */
export const buildEventAnalysisUrl = (selected, queryOperator, contextDetails, refocus) => {
  const metaFormatMap = _prepareMetaFormatMap(contextDetails.language);
  const href = windowProxy.currentUri().split('?');

  // If request to drill down is coming from an external engine (not investigate-events), we can't use
  // url to extract information like service, startTime, endTime.
  // Make use of contextDetails instead. Send out such params in this object so that
  // they can be used to create a well formed investigate/events url.
  if (!href[0].includes('investigate/events')) {
    return _buildUrlFromQueryInputs(selected, queryOperator, contextDetails, metaFormatMap);
  }
  const queryParam = (href.length > 1) ? href[1] : '';
  const queryParamArray = queryParam.split('&');
  const mf = queryParamArray.find((param) => _.startsWith(param, 'mf='));
  // Need to close recon for all scenario.
  let filteredParams = queryParamArray.filter((param) => !_.startsWith(param, 'eid=') && !_.startsWith(param, 'mf='));
  // queryParam = queryParam.replace(/eid=/, '');
  const query = buildQuery([{ meta: selected.metaName, value: selected.metaValue, operator: queryOperator }], metaFormatMap);
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

/**
 * Proxy for linkToFileAction
 */
export const buildInvestigateEventsFileLinkUrl = (file, endpointId) => {
  const investigateEventsPath = '/investigate/events';
  if (!file) {
    return;
  }
  const { start, end } = file;
  let { query = '' } = file;

  // Remove surrounding quotes from query, if any
  const hasSurroundingQuotes = query.match(/^"(.*)"$/);
  if (hasSurroundingQuotes) {
    query = hasSurroundingQuotes[1];
  }
  if (query && start && end) {
    // query is a string that looks like 'ip.dst=192.168.90.92&&tcp.dstport=49419&&ip.src=160.176.226.63'
    const qp = {
      et: end,
      mf: encodeURIComponent(query),
      sid: endpointId,
      st: start,
      mps: 'min'
    };
    query = serializeQueryParams(qp);
    const { location } = window;
    const path = `${location.origin}${investigateEventsPath}?${query}`;
    return path;
  }
};

/**
 * Creates a serialized representation of an array suitable for use in a URL query string.
 * @public
 */
const serializeQueryParams = (qp = []) => {
  const keys = Object.keys(qp);
  const values = Object.values(qp);
  return keys.map((d, i) => `${d}=${values[i]}`).join('&');
  // Once we drop IE11 we should be able to use Object.entries
  // return Object.entries(qp).map((d) => `${d[0]}=${d[1]}`).join('&');
};

/**
 * Builds a investigate-events url for context menu options that are coming from an external
 * engine/addon, like recon addon used in sa, respond.
 */
const _buildUrlFromQueryInputs = (selected, queryOperator, contextDetails, metaFormatMap) => {
  const query = buildQuery([{ meta: selected.metaName, value: selected.metaValue, operator: queryOperator }], metaFormatMap);
  const investigateEventsUrl = '/investigate/events';
  const { serviceId, startTime, endTime } = contextDetails;
  const qp = {
    et: endTime,
    mf: encodeURI(encodeURIComponent(query)),
    sid: serviceId,
    st: startTime,
    rs: 'min'
  };
  const serializedParams = serializeQueryParams(qp);
  const path = `${investigateEventsUrl}?${serializedParams}`;
  return path;
};


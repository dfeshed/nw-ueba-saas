import { helper } from '@ember/component/helper';
import copyToClipboard from 'component-lib/utils/copy-to-clipboard';
import { lookup } from 'ember-dependency-lookup';
import moment from 'moment';

const _openUrl = function(url) {
  window.open(url);
};

const _prepareMetaFormatMap = function(language) {
  if (!language) {
    return {};
  }
  return language.reduce(function(hash, { metaName, format }) {
    hash[metaName] = format;
    return hash;
  }, {});
};

const _buildQuery = function(conditions = [], metaFormatMap = {}) {
  return conditions.map((condition) => {
    const metaFormat = metaFormatMap[condition.meta];
    const { meta, value, operator } = condition;
    // if the metavalue already starts with a quote, don't add additional quotes
    //  e.g 'a``j1.[]1restonvirginia.sys' already has quotes around it, so not adding any quotes
    //  e.g a``j1.[]1restonvirginia.sys does not have outer quotes, so needs quotes or else classic blows up
    const surroundInQuotes = String(metaFormat).toLowerCase() === 'text' && value.trim().search(/[\'\"]/g) !== 0;
    const valueEncoded = surroundInQuotes ? `'${String(value)}'` : value;
    return `${meta} ${operator} ${valueEncoded}`;
  }).join(' && ');
};

export function _buildInvestigateUrl(selected, queryOperator, contextDetails, discardParentQuery) {
  const { metaName } = selected;
  let { metaValue } = selected;
  if (typeof metaValue === 'string') {
    // change metavalue NT Service\MSSQLSERVER to NT Service\\MSSQLSERVER so that on pivoting to classic, the query does not blow up.
    metaValue = metaValue.replace(/\\/g, '\\\\');
    // change metavalue 'eu'Tl' to 'eu\'Tl'
    metaValue = metaValue.replace(/[\'\"]/g, '\\\'');
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
}

export function _buildHostsUrl(selected, contextDetails) {
  const metaFormatMap = _prepareMetaFormatMap(contextDetails.language);
  const query = _buildQuery([{ meta: selected.metaName, value: selected.metaValue, operator: '=' }], metaFormatMap);
  return `/investigate/hosts?query=${encodeURIComponent(query)}`;
}

const _getTranslated = function(i18n, i18nKey) {
  // i18n.t() returns a SafeString object. Later, when this is converted to immutable before storing in redux, the
  // SafeString object is converted to Immutable object causing the toString() to be overriden. This results in
  // the string rendered as [object Object]. To avoid this, convert SafeString to plain string here itself.
  return i18n.t(i18nKey).toString();
};

export function buildContextMenu() {
  const i18n = lookup('service:i18n');
  return [
    {
      label: _getTranslated(i18n, 'recon.contextmenu.copy'),
      action(selection) {
        copyToClipboard(selection[0].metaValue);
      }
    },
    {
      label: _getTranslated(i18n, 'recon.contextmenu.refocus'),
      action(selection, contextDetails) {
        // Current query is discarded in case of refocus
        _openUrl(_buildInvestigateUrl(selection[0], '=', contextDetails, true));
      }
    },
    {
      label: _getTranslated(i18n, 'recon.contextmenu.applyDrill'),
      action(selection, contextDetails) {
        _openUrl(_buildInvestigateUrl(selection[0], '=', contextDetails));
      }
    },
    {
      label: _getTranslated(i18n, 'recon.contextmenu.applyNEDrill'),
      action(selection, contextDetails) {
        _openUrl(_buildInvestigateUrl(selection[0], '!=', contextDetails));
      }
    },
    {
      label: _getTranslated(i18n, 'recon.contextmenu.hostslookup'),
      action(selection, contextDetails) {
        _openUrl(_buildHostsUrl(selection[0], contextDetails));
      }
    },
    {
      label: _getTranslated(i18n, 'recon.contextmenu.endpointIoc'),
      action(selection) {
        _openUrl(`ecatui://${selection[0].metaValue}`);
      }
    },
    {
      label: _getTranslated(i18n, 'recon.contextmenu.livelookup'),
      action(selection) {
        _openUrl(`/live/search?metaValue=${selection[0].metaValue}`);
      }
    },
    {
      label: _getTranslated(i18n, 'recon.contextmenu.externalLinks'),
      subActions: [
        {
          label: _getTranslated(i18n, 'recon.contextmenu.external.google'),
          action(selection) {
            _openUrl(`http://www.google.com/search?q=${selection[0].metaValue}`);
          }
        },
        {
          label: _getTranslated(i18n, 'recon.contextmenu.external.sansiphistory'),
          action(selection) {
            _openUrl(`http://isc.sans.org/ipinfo.html?ip=${selection[0].metaValue}`);
          }
        },
        {
          label: _getTranslated(i18n, 'recon.contextmenu.external.centralops'),
          action(selection) {
            _openUrl(`http://centralops.net/co/DomainDossier.aspx?addr=${selection[0].metaValue}&amp;dom_whois=true&amp;dom_dns=true&amp;net_whois=true`);
          }
        },
        {
          label: _getTranslated(i18n, 'recon.contextmenu.external.robtexipsearch'),
          action(selection) {
            _openUrl(`http://www.robtex.com/ip/${selection[0].metaValue}.html`);
          }
        },
        {
          label: _getTranslated(i18n, 'recon.contextmenu.external.ipvoid'),
          action(selection) {
            _openUrl(`http://www.ipvoid.com/scan/${selection[0].metaValue}/`);
          }
        },
        {
          label: _getTranslated(i18n, 'recon.contextmenu.external.urlvoid'),
          action(selection) {
            _openUrl(`http://www.urlvoid.com/scan/${selection[0].metaValue}/`);
          }
        },
        {
          label: _getTranslated(i18n, 'recon.contextmenu.external.threatexpert'),
          action(selection) {
            _openUrl(`http://www.threatexpert.com/reports.aspx?find=${selection[0].metaValue}`);
          }
        }
      ]
    }
  ];
}

export default helper(buildContextMenu);

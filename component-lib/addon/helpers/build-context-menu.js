import { helper } from 'ember-helper';
import copyToClipboard from 'recon/utils/copy-to-clipboard';
import { lookup } from 'ember-dependency-lookup';
import moment from 'moment';

const _openUrl = function(url) {
  window.open(url);
};

const _encloseInQuotes = function(value) {
  // enclose in quotes only if the value has space char in it
  return value.indexOf(' ') > 0 ? `'${value}'` : value;
};

const _buildInvestigateUrl = function({ endpointId, metaName, metaValue, startTime, endTime }, queryOperator) {
  const query = encodeURIComponent(metaName.concat(' ', queryOperator, ' ', _encloseInQuotes(metaValue)));
  const formattedStartDate = moment(startTime > 0 ? startTime * 1000 : startTime).tz('utc').format();
  const formattedEndDate = moment(endTime > 0 ? endTime * 1000 : endTime).tz('utc').format();
  return `/investigation/endpointid/${endpointId}/navigate/query/${query}/date/${formattedStartDate}/${formattedEndDate}`;
};

export function buildContextMenu() {
  const i18n = lookup('service:i18n');
  return [
    {
      label: i18n.t('recon.contextmenu.copy'),
      action(selection) {
        copyToClipboard(selection[0].metaValue);
      }
    },
    {
      label: i18n.t('recon.contextmenu.livelookup'),
      action(selection) {
        _openUrl(`/live/search?metaValue=${selection[0].metaValue}`);
      }
    },
    {
      label: i18n.t('recon.contextmenu.endpointIoc'),
      action(selection) {
        _openUrl(`ecatui://${selection[0].metaValue}`);
      }
    },
    {
      label: i18n.t('recon.contextmenu.applyDrill'),
      action(selection) {
        _openUrl(_buildInvestigateUrl(selection[0], '='));
      }
    },
    {
      label: i18n.t('recon.contextmenu.applyNEDrill'),
      action(selection) {
        _openUrl(_buildInvestigateUrl(selection[0], '!='));
      }
    },
    {
      label: i18n.t('recon.contextmenu.externalLinks'),
      subActions: [
        {
          label: i18n.t('recon.contextmenu.external.google'),
          action(selection) {
            _openUrl(`http://www.google.com/search?q=${selection[0].metaValue}`);
          }
        },
        {
          label: i18n.t('recon.contextmenu.external.virustotal'),
          action(selection) {
            _openUrl(`https://www.virustotal.com/en/domain/${selection[0].metaValue}/information/`);
          }
        },
        {
          label: i18n.t('recon.contextmenu.external.sansiphistory'),
          action(selection) {
            _openUrl(`http://isc.sans.org/ipinfo.html?ip=${selection[0].metaValue}`);
          }
        },
        {
          label: i18n.t('recon.contextmenu.external.centralops'),
          action(selection) {
            _openUrl(`http://centralops.net/co/DomainDossier.aspx?addr=${selection[0].metaValue}&amp;dom_whois=true&amp;dom_dns=true&amp;net_whois=true`);
          }
        },
        {
          label: i18n.t('recon.contextmenu.external.robtexipsearch'),
          action(selection) {
            _openUrl(`http://www.robtex.com/ip/${selection[0].metaValue}.html`);
          }
        },
        {
          label: i18n.t('recon.contextmenu.external.ipvoid'),
          action(selection) {
            _openUrl(`http://www.ipvoid.com/scan/${selection[0].metaValue}/`);
          }
        },
        {
          label: i18n.t('recon.contextmenu.external.urlvoid'),
          action(selection) {
            _openUrl(`http://www.urlvoid.com/scan/${selection[0].metaValue}/`);
          }
        },
        {
          label: i18n.t('recon.contextmenu.external.threatexpert'),
          action(selection) {
            _openUrl(`http://www.threatexpert.com/reports.aspx?find=${selection[0].metaValue}`);
          }
        }
      ]
    }
  ];
}

export default helper(buildContextMenu);
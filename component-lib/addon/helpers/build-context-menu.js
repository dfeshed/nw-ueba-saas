import { helper } from 'ember-helper';
import copyToClipboard from 'recon/utils/copy-to-clipboard';
import { lookup } from 'ember-dependency-lookup';

const _openUrl = function(url) {
  window.open(url, '_blank').focus();
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
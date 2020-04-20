import { computed } from '@ember/object';
import layout from './template';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { getTimeWindow } from 'context/utils/context-data-modifier';


const stateToComputed = ({ context: { context: { lookupData }, tabs: { activeTabName } } }) => ({
  lookupData,
  activeTabName
});
// Need not to show footer for below data sources as there always going to be single infomration.
const footerExcludedTabs = ['LiveConnect-Ip', 'LiveConnect-Domain', 'LiveConnect-File'];
const footerIncludedTabs = ['Incidents', 'Alerts', 'Users'];
const FooterComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__footer',

  dataSourceData: computed('lookupData.[]', 'activeTabName', function() {
    const [lookupData] = this.lookupData;
    if (!lookupData) {
      return;
    }
    return lookupData[this.activeTabName === 'Endpoint' ? 'Machines' : this.activeTabName];
  }),

  footerTimeStamp: computed('dataSourceData', function() {
    return getTimeWindow(this.dataSourceData, this.get('i18n'));
  }),

  dSResultCount: computed('dataSourceData', 'activeTabName', function() {
    if (footerExcludedTabs.includes(this.activeTabName)) {
      return '';
    } else {
      const count = this.dataSourceData && this.dataSourceData.resultList ? this.dataSourceData.resultList.length : 0;
      const dataSource = `${count } ${ this.get('i18n').t(`context.footer.title.${this.activeTabName.camelize()}`)}`;
      if (footerIncludedTabs.includes(this.activeTabName)) {
        if (this.dataSourceData && this.dataSourceData.resultMeta) {
          return `${dataSource } ${ this.get('i18n').t('context.footer.resultCount', { count: this.dataSourceData.resultMeta.limit })}`;
        }
      }
      return dataSource;
    }
  })
});
export default connect(stateToComputed)(FooterComponent);

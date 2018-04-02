import { connect } from 'ember-redux';
import Component from '@ember/component';
import layout from './template';
import { isEmpty } from '@ember/utils';
import { getArcherUrl, getArcherErrorMessage } from 'context/reducers/tabs/selectors';
import { inject as service } from '@ember/service';

const stateToComputed = ({ context: { context, tabs } }) => ({
  archerUrl: getArcherUrl(tabs, context),
  archerErrorMessage: getArcherErrorMessage(tabs, context)
});

const PivotToArcherComponent = Component.extend({
  layout,

  classNames: 'rsa-context-panel__linkButton',

  flashMessages: service(),

  i18n: service(),

  actions: {
      /*
      * Pivot to archer will open the archer url on click of 'Pivot to Archer' button. It may land to login page or device details page based on the information available.
      * Sample Pivot to archer url: http://localhost:4200/RSAarcher/default.aspx?requestUrl=..%2fGenericContent%2fRecord.aspx%3fid%3d224935%26moduleId%3d71
      */
    pivotToArcher() {
      const archerUrl = this.get('archerUrl');
      const archerErrorMessage = this.get('archerErrorMessage');
      if (!isEmpty(archerUrl)) {
        window.open(archerUrl);
      } else if (archerErrorMessage.errorType === 'Warning') {
        this.get('flashMessages').warning(this.get('i18n').t(archerErrorMessage.errorMessage));
      } else if (archerErrorMessage.errorType === 'Error') {
        this.get('flashMessages').error(this.get('i18n').t(archerErrorMessage.errorMessage));
      }
    }
  }
});

export default connect(stateToComputed)(PivotToArcherComponent);

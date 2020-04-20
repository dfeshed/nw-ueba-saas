import Controller from '@ember/controller';
import { inject as service } from '@ember/service';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';

export default Controller.extend({

  standardErrors: service(),

  actions: {
    displayCommonMessage() {
      const errorObj = handleInvestigateErrorCode({ errorCode: 110 });
      this.get('standardErrors').display(errorObj);
    },

    displayUncommonMessage() {
      const errorObj = handleInvestigateErrorCode({ errorCode: 203 });
      this.get('standardErrors').display(errorObj);
    }
  }
});

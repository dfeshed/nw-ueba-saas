import Mixin from '@ember/object/mixin';
import { inject as service } from '@ember/service';

export default Mixin.create({

  contextualHelp: service(),

  actions: {

    goToGlobalHelp() {
      this.get('contextualHelp').goToGlobalHelp();
    },

    goToHelp(module, topic) {
      this.get('contextualHelp').goToHelp(module, topic);
    }

  }
});

import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default Route.extend({

  contextualHelp: service(),

  redux: service(),

  beforeModel() {
    this.replaceWith('files');
  }
});

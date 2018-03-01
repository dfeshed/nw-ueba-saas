import Controller from '@ember/controller';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

export default Controller.extend({

  flashMessages: service(),

  @computed('flashMessages')
  scope: (flashMessages) => ({ flashMessages }),

  contextItems: [
    {
      label: 'Take action',
      action(selection, contextDetails) {
        contextDetails.flashMessages.success(`Took action on ${selection}`);
      }
    },
    {
      label: 'Sub menu',
      subActions: [
        {
          label: 'Take another action',
          action(selection, contextDetails) {
            contextDetails.flashMessages.success(`Took action on ${selection}`);
          }
        }
      ]
    }
  ]
});

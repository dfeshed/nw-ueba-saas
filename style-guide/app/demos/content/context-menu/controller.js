import Ember from 'ember';
import service from 'ember-service/inject';
import computed from 'ember-computed-decorators';

const { Controller } = Ember;

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
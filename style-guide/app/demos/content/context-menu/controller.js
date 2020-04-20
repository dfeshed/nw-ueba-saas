import { computed } from '@ember/object';
import Controller from '@ember/controller';
import { inject as service } from '@ember/service';

export default Controller.extend({

  flashMessages: service(),

  scope: computed('flashMessages', function() {
    return { flashMessages: this.flashMessages };
  }),

  init() {
    this._super(arguments);
    this.contextItems = this.contextItems || [
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
    ];
  }
});

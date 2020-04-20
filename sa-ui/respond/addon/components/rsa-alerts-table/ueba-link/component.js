import { computed } from '@ember/object';
import Component from '@ember/component';
import { inject as service } from '@ember/service';

export default Component.extend({
  accessControl: service(),
  testId: 'respondUebaLink',
  attributeBindings: ['testId:test-id'],
  classNames: ['respond-ueba-link'],

  hasPermissions: computed('accessControl.hasUEBAAccess', function() {
    return this.accessControl?.hasUEBAAccess;
  }),

  ueba: computed('alert.entity_id', 'alert.classifier_id', 'alert.id', function() {
    // eslint-disable-next-line camelcase
    if (this.alert?.entity_id && this.alert?.classifier_id) {
      // eslint-disable-next-line camelcase
      const url = `/user/${this.alert?.entity_id}/alert/${this.alert?.classifier_id}`;
      return this.alert?.id ? `${url}/indicator/${this.alert?.id}` : url;
    }
  }),

  show: computed('ueba', 'hasPermissions', function() {
    return this.ueba && this.hasPermissions;
  })
});

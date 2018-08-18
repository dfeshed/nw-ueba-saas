import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

export default Component.extend({
  accessControl: service(),
  testId: 'respondUebaLink',
  attributeBindings: ['testId:test-id'],
  classNames: ['respond-ueba-link'],

  @computed('accessControl.hasUEBAAccess')
  hasPermissions(hasUEBAAccess) {
    return hasUEBAAccess;
  },

  @computed('alert.entity_id', 'alert.classifier_id', 'alert.id')
  ueba(entityId, classifierId, id) {
    if (entityId && classifierId) {
      const url = `/user/${entityId}/alert/${classifierId}`;
      return id ? `${url}/indicator/${id}` : url;
    }
  },

  @computed('ueba', 'hasPermissions')
  show(ueba, hasPermissions) {
    return ueba && hasPermissions;
  }
});

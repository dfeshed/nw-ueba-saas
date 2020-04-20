import { get, computed } from '@ember/object';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { htmlSafe } from '@ember/string';
import { getInspectorWidth } from 'respond/selectors/incidents';

const stateToComputed = (state) => ({
  width: getInspectorWidth(state)
});

const UebaWrapper = Component.extend({
  testId: 'uebaRespondWrapper',
  attributeBindings: ['testId:test-id'],
  classNames: ['ueba-standalone-container'],
  contextualHelp: service(),

  resolvedWidth: computed('width', function() {
    return htmlSafe(`width: calc(100% - ${this.width}px);`);
  }),

  entityModel: computed('ueba', function() {
    if (!this.ueba) {
      return null;
    }
    const [, entityId] = this.ueba.match(/user\/(.*)\/alert/i);
    const [, alertId] = this.ueba.match(/alert\/(.*)\/indicator/i);
    const [, indicatorId] = this.ueba.match(/indicator\/(.*)/i);
    return {
      entityType: 'user',
      showOnlyIndicator: true,
      entityId,
      alertId,
      indicatorId
    };
  }),
  actions: {
    close() {
      get(this, 'uebaClose')();
    },
    goToHelp(module, topic) {
      get(this, 'contextualHelp').goToHelp(module, topic);
    }
  }
});

export default connect(stateToComputed)(UebaWrapper);

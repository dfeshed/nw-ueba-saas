import { get } from '@ember/object';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { htmlSafe } from '@ember/string';
import computed from 'ember-computed-decorators';
import { getInspectorWidth } from 'respond/selectors/incidents';

const stateToComputed = (state) => ({
  width: getInspectorWidth(state)
});

const UebaWrapper = Component.extend({
  testId: 'uebaRespondWrapper',
  attributeBindings: ['testId:test-id'],
  classNames: ['ueba-standalone-container'],
  contextualHelp: service(),

  @computed('width')
  resolvedWidth(width) {
    return htmlSafe(`width: calc(100% - ${width}px);`);
  },
  @computed('ueba')
  entityModel(ueba) {
    if (!ueba) {
      return null;
    }
    const [, entityId] = ueba.match(/user\/(.*)\/alert/i);
    const [, alertId] = ueba.match(/alert\/(.*)\/indicator/i);
    const [, indicatorId] = ueba.match(/indicator\/(.*)/i);
    return {
      entityType: 'user',
      showOnlyIndicator: true,
      entityId,
      alertId,
      indicatorId
    };
  },
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

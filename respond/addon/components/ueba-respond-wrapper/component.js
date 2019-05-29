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

import { get } from '@ember/object';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { htmlSafe } from '@ember/string';
import computed from 'ember-computed-decorators';
import { getInspectorWidth } from 'respond/selectors/incidents';

const stateToComputed = (state) => ({
  width: getInspectorWidth(state)
});

const ReconWrapper = Component.extend({
  testId: 'reconRespondWrapper',
  attributeBindings: ['testId:test-id'],
  classNames: ['recon-standalone-container'],
  language: {},
  aliases: {},
  @computed('width')
  resolvedWidth(width) {
    return htmlSafe(`width: calc(100% - ${width}px);`);
  },
  actions: {
    close() {
      get(this, 'reconClose')();
    }
  }
});

export default connect(stateToComputed)(ReconWrapper);

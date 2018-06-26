import { get } from '@ember/object';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { htmlSafe } from '@ember/string';
import computed from 'ember-computed-decorators';
import { getInspectorWidth } from 'respond/selectors/incidents';
import { loadingRecon, getLanguage, getAliases } from 'respond/reducers/respond/recon/selectors';

const stateToComputed = (state) => ({
  width: getInspectorWidth(state),
  aliases: getAliases(state),
  language: getLanguage(state),
  loadingRecon: loadingRecon(state)
});

const ReconWrapper = Component.extend({
  testId: 'reconRespondWrapper',
  attributeBindings: ['testId:test-id'],
  classNames: ['recon-standalone-container'],
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

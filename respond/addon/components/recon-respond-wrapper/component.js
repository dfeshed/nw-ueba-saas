import { get, computed } from '@ember/object';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { htmlSafe } from '@ember/string';
import { getInspectorWidth } from 'respond/selectors/incidents';
import { loadingRecon, getLanguage, getAliases } from 'respond/reducers/respond/recon/selectors';
import moment from 'moment';

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
  contextualHelp: service(),

  resolvedWidth: computed('width', function() {
    return htmlSafe(`width: calc(100% - ${this.width}px);`);
  }),

  queryInputs: computed('endpointId', function() {
    const now = moment();
    // Default to a time range of last 7 days since
    const queryInputs = {
      endTime: now.unix(),
      serviceId: this.endpointId,
      startTime: now.subtract(7, 'days').unix()
    };
    return queryInputs;
  }),

  actions: {
    close() {
      get(this, 'reconClose')();
    },
    goToHelp(module, topic) {
      get(this, 'contextualHelp').goToHelp(module, topic);
    }
  }
});

export default connect(stateToComputed)(ReconWrapper);

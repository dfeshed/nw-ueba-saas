import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import layout from './template';
import { processAnalysisQueryString, isProcessAnalysisDisabled } from 'recon/reducers/meta/selectors';

const stateToComputed = (state) => ({
  processAnalysisQueryString: processAnalysisQueryString(state),
  isDisabled: isProcessAnalysisDisabled(state)
});

const PivotToProcessAnalysis = Component.extend({
  layout,

  attributeBindings: ['title'],
  classNames: ['pivot-to-process-analysis'],

  i18n: service(),

  @computed('i18n.locale')
  title() {
    return this.get('i18n').t('recon.textView.pivotToProcessAnalysisTitle');
  },

  actions: {
    goToProcessAnalysis() {
      const processAnalysisQueryString = this.get('processAnalysisQueryString');
      window.open(`${window.location.origin}/investigate/process-analysis?${processAnalysisQueryString}`, '_blank', 'width=1440,height=900');
    }
  }
});

export default connect(stateToComputed)(PivotToProcessAnalysis);

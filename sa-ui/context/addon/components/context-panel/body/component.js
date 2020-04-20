import { computed } from '@ember/object';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import layout from './template';
import { getActiveDataSource } from 'context/reducers/tabs/selectors';


const stateToComputed = ({ context: { context: { lookupData, meta }, tabs } }) => ({
  activedataSource: getActiveDataSource(tabs),
  activeTabName: tabs.activeTabName,
  meta,
  lookupData
});

const BodyComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__body',

  bodyStyleClass: computed('activeTabName', 'model.contextData.liveConnectData', function() {
    return this.activeTabName === 'liveConnect' && this.model?.contextData?.liveConnectData ? 'rsa-context-panel__body feedback-margin' : 'rsa-context-panel__body';
  })

});

export default connect(stateToComputed)(BodyComponent);

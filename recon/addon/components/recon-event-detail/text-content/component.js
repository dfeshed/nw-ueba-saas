import Ember from 'ember';
import ReconPager from 'recon/mixins/recon-pager';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import layout from './template';
import { isLogEvent } from 'recon/selectors/event-type-selectors';

const { Component } = Ember;

const stateToComputed = ({ recon, recon: { data, visuals } }) => ({
  eventType: data.eventType,
  textContent: data.textContent,
  eventTotal: data.total,
  dataIndex: data.index,
  isLogEvent: isLogEvent(recon),
  isRequestShown: visuals.isRequestShown,
  isResponseShown: visuals.isResponseShown
});

const TextReconComponent = Component.extend(ReconPager, {
  classNames: ['recon-event-detail-text'],
  layout,

  @computed('textContent', 'isRequestShown', 'isResponseShown')
  filteredContent(textContent, isRequestShown, isResponseShown) {
    if (isRequestShown && isResponseShown) {
      return textContent;
    } else {
      return textContent.filter((text) => {
        return (text.side === 'request' && isRequestShown) ||
               (text.side === 'response' && isResponseShown);
      });
    }
  }
});

export default connect(stateToComputed)(TextReconComponent);

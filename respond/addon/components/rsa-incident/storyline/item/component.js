import Ember from 'ember';
import ListItem from 'respond/components/rsa-list/item/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';
import * as UIStateActions from 'respond/actions/ui-state-creators';

const { get } = Ember;

const stateToComputed = () => ({ });

const dispatchToActions = (dispatch) => ({
  clickAction: (item) => dispatch(UIStateActions.singleSelectStoryPoint(item && get(item, 'id'))),
  ctrlClickAction: (item) => dispatch(UIStateActions.toggleSelectStoryPoint(item && get(item, 'id'))),
  shiftClickAction: (item) => dispatch(UIStateActions.toggleSelectStoryPoint(item && get(item, 'id')))
});

// Ordered list of enrichments to be displayed.
const enrichmentsToDisplay = [
  {
    dataKey: 'ctxhub.domain_is_whitelisted',
    threshold: 0
  }, {
    dataKey: 'smooth.smooth_beaconing_score',
    threshold: 50
  }, {
    dataKey: 'new_domain.age_score',
    threshold: 50
  }, {
    dataKey: 'whois.age_score',
    threshold: 50
  }, {
    dataKey: 'whois.validity_score',
    threshold: 50
  }, {
    dataKey: 'domain.referer_score',
    threshold: 50
  }, {
    dataKey: 'domain.referer_ratio_score',
    threshold: 50
  }, {
    dataKey: 'domain.ua_ratio_score',
    threshold: 50
  }
];

/**
 * @class Storyline Item component
 * A subclass of List Item component which renders data from a storypoint object, including the summary information
 * about the storypoint's corresponding indicator, and the enrichments in the indicator's events (if any).
 * @public
 */
const StorylineItem = ListItem.extend({
  tagName: 'vbox',
  classNames: ['rsa-incident-storyline-item'],
  classNameBindings: ['item.isCatalyst:is-catalyst', 'item.isHidden:is-hidden'],
  layout,
  enrichmentsToDisplay,

  @computed('item.matched')
  resolvedMatched(matched = []) {
    const types = [ 'user', 'host', 'domain', 'ip', 'ip', 'file' ];
    return matched
      .map((id, index) => ({ type: types[index], id }))
      .rejectBy('id', '');
  }
});

export default connect(stateToComputed, dispatchToActions)(StorylineItem);

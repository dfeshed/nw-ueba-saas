import get from 'ember-metal/get';
import ListItem from 'respond/components/rsa-list/item/component';
import HighlightsEntities from 'context/mixins/highlights-entities';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import {
  singleSelectStoryPoint,
  toggleSelectStoryPoint
} from 'respond/actions/creators/incidents-creators';

const stateToComputed = () => ({ });

const dispatchToActions = (dispatch) => ({
  clickAction: (item) => dispatch(singleSelectStoryPoint(item && get(item, 'id'))),
  ctrlClickAction: (item) => dispatch(toggleSelectStoryPoint(item && get(item, 'id'))),
  shiftClickAction: (item) => dispatch(toggleSelectStoryPoint(item && get(item, 'id')))
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
const StorylineItem = ListItem.extend(HighlightsEntities, {
  tagName: 'vbox',
  classNames: ['rsa-incident-storyline-item'],
  classNameBindings: ['isCatalyst', 'item.isHidden:is-hidden'],
  layout,
  enrichmentsToDisplay,

  // Specifies the endpoint from which the normalized event objects originated (in this case, 'IM' refers to the
  // Netwitness Incident Management module). Used by the HighlightEntities mixin to map event properties to entity types.
  // @see context/mixins/highlights-entities
  autoHighlightEntities: true,
  entityEndpointId: 'IM',

  // Returns `true` if the `incidentId` & `storylineId` of `item` match.
  @computed('item.indicator.incidentId', 'item.indicator.storylineId')
  isCatalyst(incidentId, storylineId) {
    return incidentId === storylineId;
  }
});

export default connect(stateToComputed, dispatchToActions)(StorylineItem);

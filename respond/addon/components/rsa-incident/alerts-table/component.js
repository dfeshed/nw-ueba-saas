import AlertsTable from 'respond/components/rsa-alerts-table/component';
import { get } from '@ember/object';
import { connect } from 'ember-redux';
import { storyPointsWithEventsSorted } from 'respond/selectors/storyline';
import {
  singleSelectStoryPoint,
  toggleSelectStoryPoint,
  singleSelectEvent,
  toggleSelectEvent
} from 'respond/actions/creators/incidents-creators';

const stateToComputed = (state) => {
  const { respond: { incident: { selection: { type, ids } } } } = state;
  return {
    groups: storyPointsWithEventsSorted(state),
    selections: {
      areGroups: type === 'storyPoint',
      ids: (type === 'storyPoint' || type === 'event') ? ids : []
    }
  };
};


const dispatchToActions = (dispatch) => ({
  groupClickAction: (clickData) => dispatch(singleSelectStoryPoint(clickData && get(clickData, 'group.id'))),
  groupCtrlClickAction: (clickData) => dispatch(toggleSelectStoryPoint(clickData && get(clickData, 'group.id'))),
  groupShiftClickAction: (clickData) => dispatch(toggleSelectStoryPoint(clickData && get(clickData, 'group.id'))),
  itemClickAction: (clickData) => dispatch(singleSelectEvent(clickData && get(clickData, 'item.id'))),
  itemCtrlClickAction: (clickData) => dispatch(toggleSelectEvent(clickData && get(clickData, 'item.id'))),
  itemShiftClickAction: (clickData) => dispatch(toggleSelectEvent(clickData && get(clickData, 'item.id')))
});

export default connect(stateToComputed, dispatchToActions)(AlertsTable);

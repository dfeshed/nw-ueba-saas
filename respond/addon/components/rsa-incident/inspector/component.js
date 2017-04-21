import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import * as UIStateActions from 'respond/actions/ui-state-creators';
import { storyPointCount, storyEventCount } from 'respond/selectors/storyline';
import CspStyleMixin from 'ember-cli-csp-style/mixins/csp-style';
import DragBehavior from 'respond/utils/behaviors/drag';

const {
  $,
  Component
} = Ember;

const stateToComputed = (state) => {
  const { respond: { incident: { id, info, infoStatus, viewMode, inspectorWidth } } } = state;
  return {
    incidentId: id,
    info,
    infoStatus,
    viewMode,
    width: inspectorWidth,
    storyPointCount: storyPointCount(state),
    storyEventCount: storyEventCount(state)
  };
};

const dispatchToActions = (dispatch) => ({
  setViewModeAction(viewMode) {
    dispatch(UIStateActions.setViewMode(viewMode));
  },
  resizeAction(width) {
    dispatch(UIStateActions.resizeIncidentInspector(width));
  }
});

const MINIMUM_WIDTH = 350;

const IncidentInspector = Component.extend(CspStyleMixin, {
  tagName: 'article',
  classNames: ['rsa-incident-inspector'],
  classNameBindings: ['isResizing'],
  styleBindings: ['resolvedWidth:width[px]'],
  incidentId: null,
  info: null,
  infoStatus: null,
  viewMode: null,
  width: null,
  isResizing: false,
  setViewModeAction: null,
  storyPointCount: null,
  storyEventCount: null,

  // Same as `width`, but enforces minimum & maximum.
  @computed('width')
  resolvedWidth(width) {
    return Math.max((width || 0), MINIMUM_WIDTH);
  },

  // Wire up a drag behavior on resizer DOM node to invoke the "resizeAction".
  didInsertElement() {
    const dragstart = () => {
      this.setProperties({
        widthWas: this.get('width') || 0,
        isResizing: true
      });
    };
    const dragmove = (e, ctxt) => {
      const delta = ctxt.get('delta') || [];
      const width = this.get('widthWas') + delta[0];
      this.send('resizeAction', width);
    };
    const dragend = () => {
      this.set('isResizing', false);
    };
    const behavior = DragBehavior.create({
      minMouseMoves: 0,
      callbacks: {
        dragstart,
        dragmove,
        dragend
      }
    });
    $('.js-incident-inspector-resizer').on('mousedown', function(e) {
      behavior.mouseDidDown(e);
      e.preventDefault();
      return false;
    });
    this.set('dragBehavior', behavior);
  },

  // Unwire the drag behavior that was wired up in didInsertElement.
  willDestroyElement() {
    $('.js-incident-inspector-resizer').off('mousedown');
    const drag = this.get('dragBehavior');
    drag.teardown();
    this.set('dragBehavior', null);
  }
});

/**
 * @class Incident Inspector
 * A Container for displaying information about an Incident in various view modes.
 *
 * @public
 */
export default connect(stateToComputed, dispatchToActions)(IncidentInspector);
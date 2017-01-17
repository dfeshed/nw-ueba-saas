import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => {
  return {
    focusResource: state.live.search.focusResource
  };
};

/**
 * Extension of the Data Table default row class to support "focus row" styling indicating that a given row
 * is currently highlighted/placed-in-focus by the user. This is not to be confused w/ the focus/blur state used for
 * form control (and other) DOM elements. The name here, though, is used to distinguish from a "selected" row that is
 * based on checkbox selection.
 * @public
 */
const LiveSearchResultsRow = DataTableBodyRow.extend({
  classNameBindings: ['hasFocus'],

  /**
   * True if the row item is the same as the "focusResource" in app state that tracks which resource is currently
   * focused on by the user (e.g., for viewing the resource details)
   * @property hasFocus
   * @public
   */
  @computed('focusResource', 'item')
  hasFocus(focusResource, item) {
    return focusResource === item;
  }
});

export default connect(stateToComputed)(LiveSearchResultsRow);
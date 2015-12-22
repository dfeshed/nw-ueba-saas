/**
 * @file Service for getting and setting a theme to the GUI.
 * A theme is "applied" as simply a CSS class name on the app's rootElement (which is typically either <body> in
 * production, or a <div> for automated testing).
 * The CSS class for a corresponding theme id is 'rsa-<theme-id>' (e.g., 'rsa-dark').
 * Note that theme ids (e.g, 'light', 'dark', etc) are not user-friendly titles (e.g., 'Light Theme', 'Dark Theme').
 * @public
 */
import Ember from 'ember';

// Helper that retrieves the DOM element to which the theme CSS class should be applied.
function root() {
  return Ember.$(document.documentElement);
}

// Prefix applied to CSS class name corresponding to a theme id.
const _CSS_PREFIX = 'rsa-';

// Cache for currently selected theme id.
let _currentTheme;

export default Ember.Service.extend({

  /**
   * Getter & setter for id of currently selected theme.
   * @public
   */
  selected: Ember.computed({
    get() {
      return _currentTheme || '';
    },
    set(key, val) {
      let was = _currentTheme || '';
      if (was !== val) {
        _currentTheme = val;

        // Theme changed. Remove old theme CSS class (if any) and apply new CSS class.
        let $root = root();
        if (was) {
          $root.removeClass(_CSS_PREFIX + was);
        }
        $root.addClass(_CSS_PREFIX + val);
      }
      return _currentTheme;
    }
  }),

  /**
   * List of theme IDs that this service can auto-detect.
   * @type string[]
   * @public
   */
  all: [
    { id: 'light', title: 'Light Theme' },
    { id: 'dark', title: 'Dark Theme' }
  ],

  /**
   * Auto-detects the current theme by inspecting DOM CSS.
   * @public
   */
  init() {
    let $root = root();
    this.get('all').any(function(themeDef) {
      if ($root.hasClass(_CSS_PREFIX + themeDef.id)) {
        _currentTheme = themeDef.id;
        return true;
      }
      return false;
    });
  }
});

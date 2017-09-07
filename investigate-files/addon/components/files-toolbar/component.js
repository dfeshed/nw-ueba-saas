import Component from 'ember-component';
import { connect } from 'ember-redux';

const stateToComputed = ({ files }) => ({
  totalItems: files.fileList.totalItems // Total number of files in search result
});

/**
 * Toolbar that provides search filtering.
 * @public
 */
const ToolBar = Component.extend({
  tagName: 'vbox',
  classNames: 'rsa-files-toolbar-1 rsa-application-layout-panel-header col-xs-12 flexi-fit'
});
export default connect(stateToComputed)(ToolBar);
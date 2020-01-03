import classic from 'ember-classic-decorator';
import { classNames, tagName, layout as templateLayout } from '@ember-decorators/component';
import Component from '@ember/component';
import layout from './template';

@classic
@templateLayout(layout)
@tagName('hbox')
@classNames('property-name col-xs-6 col-md-5')
export default class PropertyName extends Component {}

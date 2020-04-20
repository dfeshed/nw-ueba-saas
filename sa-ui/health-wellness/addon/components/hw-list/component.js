import Component from '@ember/component';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import classic from 'ember-classic-decorator';
import layout from './template';

@classic
@classNames('hw-list')
@templateLayout(layout)
export default class HWList extends Component {}